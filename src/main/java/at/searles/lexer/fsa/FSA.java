package at.searles.lexer.fsa;

import at.searles.buf.FrameStream;
import at.searles.lexer.utils.Counter;
import at.searles.lexer.utils.IntSet;
import at.searles.lexer.utils.IntervalSet;
import at.searles.lexer.utils.LexicalSet;
import at.searles.regex.CharSet;

import java.util.*;

/**
 * This class is the FSA-Automaton for the lexer. It is always kept in a deterministic
 * state which was actually fun to leaf. Each node maintains a list of
 * all tokens that it accepts.
 */
public class FSA {

    /**
     * Counter that is used to generate new node labels.
     */
    private final Counter counter;

    /**
     * One start node
     */
    private Node start;

    /**
     * Creates an FSA that accepts nothing.
     */
    public FSA(Counter counter, boolean accept) {
        this.counter = counter;
        start = newNode(accept ? new IntSet() : null, null); // accepting state
    }

    /**
     * Creates a new FSA. Since many data are shared amongst other DFAs one has to
     * provide a parent FSA that maintains the original counter and dfaTable.
     * <p>
     * Furthermore, one CharSet is provided. The resulting FSA is
     * simply one that accepts all characters in set.
     */
    public FSA(Counter counter, CharSet set) {
        this.counter = counter;

        Node end = newNode(new IntSet(), null);
        start = newNode(null, set.copyIntervalSet(end)); // not accepting state
    }

    /**
     * Returns the last accepting node that is reached from buf. (Last because we want
     * the longest token).
     * <p>
     * In buf, the position is marked. It is the caller's responsibility to
     * call buf.clear() before proceeding to the next token.
     *
     * @param stream buffer from which characters are read. The frame
     *               is set according to the longest match.
     *               If there already is a frame defined, the result is
     *               undefined.
     * @return the accepted node, null if there is none.
     */
    public Node accept(FrameStream stream) {
        Node n = start;

        Node acceptedNode = null;

        while (n != null) {
            if (n.acceptors != null) {
                // it is an accepting state

                // XXX If ever the longest-match-policy should be
                // modified, the stream should be rather added
                // to a map with the current position.

                stream.markFrameEnd();
                acceptedNode = n;
            }

            // next char
            int ch = stream.next();

            n = n.accept(ch);
        }

        return acceptedNode;
    }

    /**
     * Creates a new node and registers it with
     * the internal data structures.
     *
     * @param transitions The transitions to be used. if null, an empty transition set is used.
     * @return The new node. If transitions was null here, it must be set before it is used!
     */
    private Node newNode(IntSet acceptors, IntervalSet<Node> transitions) {
        Node n = new Node(counter.incr());
        n.transitions = transitions == null ? new IntervalSet<>() : transitions;

        // when we create a node, it is perfectly fine that it is accepting
        // but does not accept any useful token. These nodes are created in
        // intermediate steps.
        n.acceptors = acceptors;

        return n;
    }

    public Node start() {
        return start;
    }

    public Set<Node> accepting() {
        return start.accepting();
    }

    public FSA or(FSA other) {
        DFAAlgorithm alg = new DFAAlgorithm();

        alg.addEpsilonTransition(this.start, other.start);
        alg.commit();

        return this;
    }

    public FSA then(FSA other) {
        DFAAlgorithm alg = new DFAAlgorithm();

        Set<Node> acceptingInThis = start.accepting();

        for (Node n : acceptingInThis) {
            n.acceptors = null;
        }

        for (Node n : acceptingInThis) {
            alg.addEpsilonTransition(n, other.start);
        }

        alg.commit();

        return this;
    }

    public FSA plus() {
        DFAAlgorithm alg = new DFAAlgorithm();

        for (Node n : start.accepting()) {
            alg.addEpsilonTransition(n, start);
        }

        alg.commit();

        return this;
    }

    /**
     * Makes this FSA optional
     */
    public FSA opt() {
        // this one's easy.
        if (this.start.acceptors == null) {
            this.start.acceptors = new IntSet();
        }

        return this;
    }

    /**
     * Removes all outgoing edges of accepting states so that the nonGreedy token
     * is recognized.
     *
     * @return this
     */
    public FSA shortest() {
        for (Node n : start.accepting()) {
            n.transitions.clear();
        }

        return this;
    }

    public String toGraphVizString() {
        StringBuilder sb = new StringBuilder();

        sb.append("digraph finite_state_machine {\n\trankdir=LR;\n\tsize=\"8,5\"\n\t");

        sb.append("node [shape = doublecircle];");

        for (Node n : start.accepting()) {
            sb.append(" ").append(n.toString());
        }

        sb.append(";\n\t");
        sb.append("node [shape = point]; start;\n\t");

        sb.append("node [shape = circle];\n\t");

        sb.append("start -> ").append(start).append(";\n");

        for (Node n : start.nodes()) {
            IntervalSet.Iter<Node> iterator = n.transitions.iterator();

            while (iterator.hasNext()) {
                Node m = iterator.next();

                String range = rangeString(iterator);

                sb.append("\t").append(n).append(" -> ").append(m);
                sb.append(" [label=\"").append(range).append("\"];\n");
            }
        }

        sb.append("}");

        return sb.toString();
    }

    private String rangeString(IntervalSet.Iter<Node> iterator) {
        String range = Character.toString((char) iterator.start());

        if (iterator.start() + 1 < iterator.end()) {
            range += " - " + (char) (iterator.end() - 1);
        }
        return range;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("start=").append(start).append("; ");
        sb.append("end=[");

        boolean first = true;
        for (Node n : start.accepting()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }

            sb.append(n);

            if (n.acceptors != null && n.acceptors.isEmpty()) {
                sb.append(n.acceptors);
            }
        }

        sb.append("]; ");

        for (Node n : start.nodes()) {
            IntervalSet.Iter<Node> iterator = n.transitions.iterator();

            while (iterator.hasNext()) {
                iterator.next();
                String range = rangeString(iterator);
                Node m = iterator.value();

                sb.append(n).append(" -[").append(range).append("]-> ").append(m).append("; ");
            }
        }

        return sb.toString();
    }

    /**
     * Node of this FSA. Every node has a unique id (see counter).
     * Furthermore, it maintains an adjacency list.
     */
    public static class Node implements Comparable<Node> {
        final int id;
        /**
         * Each node maintains a list of integer IDs that it accepts.
         * It may also be empty (if we ignore its value).
         * If acceptors is null, the node is not accepting.
         */
        public IntSet acceptors;
        IntervalSet<Node> transitions;
        boolean mark = false; // for mark-and-sweep

        Node(int id) {
            this.id = id;
        }

        /**
         * returns the next node for character ch.
         *
         * @param ch checks whether this node has an outgoing transition for ch.
         *           A rangeSet is used instead of a character because of the floorEntry function
         *           in TreeMap.
         * @return null, if there is no node.
         */
        Node accept(int ch) {
            return transitions.find(ch);
        }

        Set<Node> nodes() {
            Set<Node> ret = nodes(new TreeSet<>());
            unmark();
            return ret;
        }

        private Set<Node> nodes(TreeSet<Node> set) {
            if (this.mark) return set;

            this.mark = true;

            set.add(this);

            for (Node m : this.transitions) {
                m.nodes(set);
            }

            return set;
        }

        /**
         * Returns all accepting states reachable from this
         */
        Set<Node> accepting() {
            Set<Node> ret = accepting(new TreeSet<>());
            unmark();
            return ret;
        }

        private Set<Node> accepting(TreeSet<Node> set) {
            if (this.mark) return set;

            this.mark = true;

            if (this.acceptors != null) {
                set.add(this);
            }

            for (Node m : this.transitions) {
                m.accepting(set);
            }

            return set;
        }


        void unmark() {
            if (mark) {
                mark = false;
                for (Node m : transitions) {
                    m.unmark();
                }
            }
        }

        public String toString() {
            return "q" + id;
        }

        @Override
        public int compareTo(Node that) {
            return Integer.compare(this.id, that.id);
        }

    }

    /**
     * Class to apply the DFA-algorithm.
     */
    private class DFAAlgorithm {
        final Map<Node, LexicalSet<Node>> replacementMap;

        DFAAlgorithm() {
            this.replacementMap = new TreeMap<>();
        }

        /**
         * Adds an epsilon transition. In replacementMap,
         * the src-node is stored as key for the set of
         * all nodes that it is connected to. All sets
         * are kept closed, ie, if a value contains a node
         * that is replaced, its replacements are updated
         * in the value too.
         */
        void addEpsilonTransition(Node src, Node dst) {
            if (replacementMap.containsKey(src)) {
                // only one epsilon edge per node.
                throw new IllegalArgumentException();
            }

            LexicalSet<Node> set = new LexicalSet<Node>().add(src);

            LexicalSet<Node> dstSet = replacementMap.get(dst);

            if (dstSet != null) {
                set.addAll(dstSet);
            } else {
                set.add(dst);
            }

            // check whether any set in the replacement map contains
            // src.

            for (Map.Entry<Node, LexicalSet<Node>> entry : replacementMap.entrySet()) {
                if (entry.getValue().contains(src)) {
                    entry.getValue().addAll(set);
                }
            }

            replacementMap.put(src, set);
        }

        /**
         * If set contains an element in replacementMap,
         * its representation from replacementMap will be added.
         */
        LexicalSet<Node> closure(LexicalSet<Node> set) {
            for (Map.Entry<Node, LexicalSet<Node>> entry : replacementMap.entrySet()) {
                if (set.contains(entry.getKey())) {
                    set.addAll(entry.getValue());
                }
            }

            return set;
        }

        /**
         * Creates an interval set of all transitions of nodes. This is
         * an important part of determinization.
         *
         * @param nodes All nodes that are merged
         * @return an interval set of set of nodes that represents the union of
         * all transitions of nodes.
         */
        private IntervalSet<LexicalSet<Node>> mergeTransitions(LexicalSet<Node> nodes) {
            IntervalSet<LexicalSet<Node>> transitions = null;

            for (Node node : nodes) {
                IntervalSet<LexicalSet<Node>> currentTransitionsInSet = node.transitions.copy(n -> new LexicalSet<Node>().add(n));
                if (transitions == null) { // first run in loop
                    transitions = currentTransitionsInSet;
                } else {
                    transitions.add(currentTransitionsInSet, (set1, set2) ->
                            new LexicalSet<Node>().addAll(set1).addAll(set2)
                    );
                }
            }

            return transitions;
        }

        /**
         * Calculates all transitions based on current values in this object.
         */
        Map<LexicalSet<Node>, IntervalSet<LexicalSet<Node>>> transitions() {
            Queue<LexicalSet<Node>> queue = new LinkedList<>();

            Map<LexicalSet<Node>, IntervalSet<LexicalSet<Node>>> transitions = new TreeMap<>();

            for (LexicalSet<Node> set : replacementMap.values()) {
                queue.offer(set);
            }

            while (!queue.isEmpty()) {
                LexicalSet<Node> current = queue.poll();

                if (!transitions.containsKey(current)) {
                    IntervalSet<LexicalSet<Node>> dests = mergeTransitions(current);

                    // must update dests according to replacement map.

                    for (LexicalSet<Node> dest1 : dests) {
                        // replace nodes in replacement map by their set.
                        LexicalSet<Node> dest = closure(dest1);
                        // not needed it.setValue(dest);

                        if (dest.size() > 1) {
                            queue.offer(dest);
                        }
                    }

                    transitions.put(current, dests);
                }
            }

            return transitions;
        }

        IntSet acceptors(LexicalSet<Node> set) {
            IntSet acceptors = null;

            for (Node m : set) {
                if (m.acceptors != null) {
                    if (acceptors == null) {
                        acceptors = new IntSet();
                    }

                    acceptors.addAll(m.acceptors);
                }
            }

            return acceptors;
        }

        void commit() {
            Map<LexicalSet<Node>, IntervalSet<LexicalSet<Node>>> transitions = transitions();

            // Need to assign nodes
            Map<LexicalSet<Node>, Node> nodeAssignment = new TreeMap<>();

            // For better debugging I keep but update original nodes in replacement map.
            for (Map.Entry<Node, LexicalSet<Node>> entry : replacementMap.entrySet()) {
                if (!nodeAssignment.containsKey(entry.getValue())) {
                    // there might be multiple nodes that are mapped to the same.
                    nodeAssignment.put(entry.getValue(), entry.getKey());
                    // update acceptors
                    entry.getKey().acceptors = acceptors(entry.getValue());
                }
            }

            for (LexicalSet<Node> nodeUnion : transitions.keySet()) {
                if (!nodeAssignment.containsKey(nodeUnion)) {
                    nodeAssignment.put(nodeUnion, newNode(acceptors(nodeUnion), null));
                }
            }

            // Update transitions to new nodes
            // XXX optimization: This is only necessary for those nodes that are not kept.
            for (Node n : start.nodes()) {
                for (IntervalSet.Iter<Node> it = n.transitions.iterator(); it.hasNext(); ) {
                    if (replacementMap.containsKey(it.next())) {
                        it.setValue(nodeAssignment.get(replacementMap.get(it.value())));
                    }
                }
            }

            // don't forget the start node
            if (replacementMap.containsKey(start)) {
                start = nodeAssignment.get(replacementMap.get(start));
            }

            // Now, for each node, translate transitions into new node sets.
            for (Map.Entry<LexicalSet<Node>, Node> entry : nodeAssignment.entrySet()) {
                entry.getValue().transitions = transitions.get(entry.getKey()).copy(
                        set -> {
                            Node dst = nodeAssignment.get(set);
                            if (dst != null) return dst;
                            if (set.size() == 1) return set.first();

                            throw new IllegalArgumentException();
                        }
                );
            }
        }
    }
}
