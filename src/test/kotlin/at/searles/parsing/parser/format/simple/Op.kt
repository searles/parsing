package at.searles.parsing.parser.format.simple;

import kotlin.math.pow

enum class Op {
        Not {
            override fun exec(args: List<Any>): Any {
                return !(args[0] as Boolean)
            }
        }, 
        Neg {
            override fun exec(args: List<Any>): Any {
                return -(args[0] as Int)
            }
        },
        Greater {
            override fun exec(args: List<Any>): Any {
                return args[0] as Int > args[1] as Int
            }
        }, 
        GreaterEquals {
            override fun exec(args: List<Any>): Any {
                return args[0] as Int >= args[1] as Int
            }
        }, 
        Equals {
            override fun exec(args: List<Any>): Any {
                return args[0] as Int == args[1] as Int
            }
        }, 
        NotEquals {
            override fun exec(args: List<Any>): Any {
                return args[0] as Int != args[1] as Int
            }
        }, 
        SmallerEquals {
            override fun exec(args: List<Any>): Any {
                return args[0] as Int <= args[1] as Int
            }
        }, 
        Smaller {
            override fun exec(args: List<Any>): Any {
                return (args[0] as Int) < (args[1] as Int)
            }
        },
        Plus {
            override fun exec(args: List<Any>): Any {
                return args[0] as Int + args[1] as Int
            }
        }, 
        Minus {
            override fun exec(args: List<Any>): Any {
                return args[0] as Int - args[1] as Int
            }
        }, 
        Times {
            override fun exec(args: List<Any>): Any {
                return args[0] as Int * args[1] as Int
            }
        }, 
        Divide {
            override fun exec(args: List<Any>): Any {
                return args[0] as Int / args[1] as Int
            }
        }, 
        Modulo {
            override fun exec(args: List<Any>): Any {
                return args[0] as Int % args[1] as Int
            }
        }, 
        Pow {
            override fun exec(args: List<Any>): Any {
                return (args[0] as Int).toDouble().pow((args[1] as Int).toDouble()).toInt()
            }
        },
        And {
            override fun exec(args: List<Any>): Any {
                return args[0] as Boolean and args[1] as Boolean
            }
        }, 
        Or {
            override fun exec(args: List<Any>): Any {
                return args[0] as Boolean or args[1] as Boolean
            }
        };
        abstract fun exec(args: List<Any>): Any
    }