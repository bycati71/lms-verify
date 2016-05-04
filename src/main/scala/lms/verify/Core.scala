package lms.verify

import scala.lms.common._
import scala.reflect.SourceContext
import java.io.{PrintWriter,StringWriter,FileOutputStream}

trait VerifyOps extends Base with BooleanOps {
  def includes: List[String] = List("<limits.h>")
  def autoAssignNothing: Boolean = true

  case class TopLevel[B](name: String, mAs: List[Typ[_]], mB:Typ[B], f: List[Rep[_]] => Rep[B], spec: Boolean)
  object TopLevel {
    def apply[B](name: String, mAs: List[Typ[_]], mB:Typ[B], f: List[Rep[_]] => Rep[B]): TopLevel[B] =
      TopLevel(name, mAs, mB, f, spec=false)
  }
  val rec = new scala.collection.mutable.LinkedHashMap[String,TopLevel[_]]
  def mkTopLevel[B](name: String, mAs: List[Typ[_]], mB:Typ[B], f: List[Rep[_]] => Rep[B], pre: List[Rep[_]] => Rep[Boolean], post: List[Rep[_]] => Rep[B] => Rep[Boolean]) =
    TopLevel(name, mAs, mB, {xs =>
      requires{pre(xs)}
      ensures{r: Rep[B] => post(xs)(r)}(mB)
      f(xs)
    })
  def toplevel[A:Manifest:Typ,B:Manifest:Typ](name: String, f: Rep[A] => Rep[B]): Rep[A] => Rep[B] = {
    val g = (x: Rep[A]) => toplevelApply[B](name, List(x))
    rec.getOrElseUpdate(name, TopLevel(name, List(implicitly[Typ[A]]), implicitly[Typ[B]], xs => f(xs(0).asInstanceOf[Rep[A]])))
    g
  }
  def toplevel[A1:Manifest:Typ,A2:Manifest:Typ,A3:Manifest:Typ,B:Manifest:Typ](name: String, f: (Rep[A1], Rep[A2], Rep[A3]) => Rep[B]): (Rep[A1], Rep[A2], Rep[A3]) => Rep[B] = {
    val g = (x1: Rep[A1], x2: Rep[A2], x3: Rep[A3]) => toplevelApply[B](name, List(x1, x2, x3))
    rec.getOrElseUpdate(name, TopLevel(name, List(implicitly[Typ[A1]], implicitly[Typ[A2]], implicitly[Typ[A3]]), implicitly[Typ[B]], xs => f(xs(0).asInstanceOf[Rep[A1]], xs(1).asInstanceOf[Rep[A2]], xs(2).asInstanceOf[Rep[A3]])))
    g
  }

  def toplevel[A:Manifest:Typ,B:Manifest:Typ](name: String, f: Rep[A] => Rep[B], pre: Rep[A] => Rep[Boolean], post: Rep[A] => Rep[B] => Rep[Boolean]): Rep[A] => Rep[B] = {
    val g = (x: Rep[A]) => toplevelApply[B](name, List(x))
    rec.getOrElseUpdate(name, mkTopLevel(name, List(implicitly[Typ[A]]), implicitly[Typ[B]], xs => f(xs(0).asInstanceOf[Rep[A]]), xs => pre(xs(0).asInstanceOf[Rep[A]]), (xs: List[Rep[_]]) => (r: Rep[B]) => post(xs(0).asInstanceOf[Rep[A]])(r)))
    g
  }
  def toplevel[A1:Manifest:Typ,A2:Manifest:Typ,B:Manifest:Typ](name: String, f: (Rep[A1], Rep[A2]) => Rep[B], pre: (Rep[A1], Rep[A2]) => Rep[Boolean], post: (Rep[A1], Rep[A2]) => Rep[B] => Rep[Boolean]): (Rep[A1], Rep[A2]) => Rep[B] = {
    val g = (x1: Rep[A1], x2: Rep[A2]) => toplevelApply[B](name, List(x1, x2))
    rec.getOrElseUpdate(name, mkTopLevel(name, List(implicitly[Typ[A1]], implicitly[Typ[A2]]), implicitly[Typ[B]], xs => f(xs(0).asInstanceOf[Rep[A1]], xs(1).asInstanceOf[Rep[A2]]), xs => pre(xs(0).asInstanceOf[Rep[A1]], xs(1).asInstanceOf[Rep[A2]]), (xs: List[Rep[_]]) => (r: Rep[B]) => post(xs(0).asInstanceOf[Rep[A1]], xs(1).asInstanceOf[Rep[A2]])(r)))
    g
  }
  def toplevel[A1:Manifest:Typ,A2:Manifest:Typ,A3:Manifest:Typ,B:Manifest:Typ](name: String, f: (Rep[A1], Rep[A2], Rep[A3]) => Rep[B], pre: (Rep[A1], Rep[A2], Rep[A3]) => Rep[Boolean], post: (Rep[A1], Rep[A2], Rep[A3]) => Rep[B] => Rep[Boolean]): (Rep[A1], Rep[A2], Rep[A3]) => Rep[B] = {
    val g = (x1: Rep[A1], x2: Rep[A2], x3: Rep[A3]) => toplevelApply[B](name, List(x1, x2, x3))
    rec.getOrElseUpdate(name, mkTopLevel(name, List(implicitly[Typ[A1]], implicitly[Typ[A2]], implicitly[Typ[A3]]), implicitly[Typ[B]], xs => f(xs(0).asInstanceOf[Rep[A1]], xs(1).asInstanceOf[Rep[A2]], xs(2).asInstanceOf[Rep[A3]]), xs => pre(xs(0).asInstanceOf[Rep[A1]], xs(1).asInstanceOf[Rep[A2]], xs(2).asInstanceOf[Rep[A3]]), (xs: List[Rep[_]]) => (r: Rep[B]) => post(xs(0).asInstanceOf[Rep[A1]], xs(1).asInstanceOf[Rep[A2]], xs(2).asInstanceOf[Rep[A3]])(r)))
    g
  }
  def toplevel[A1:Manifest:Typ,A2:Manifest:Typ,A3:Manifest:Typ,A4:Manifest:Typ,B:Manifest:Typ](name: String, f: (Rep[A1], Rep[A2], Rep[A3], Rep[A4]) => Rep[B], pre: (Rep[A1], Rep[A2], Rep[A3], Rep[A4]) => Rep[Boolean], post: (Rep[A1], Rep[A2], Rep[A3], Rep[A4]) => Rep[B] => Rep[Boolean]): (Rep[A1], Rep[A2], Rep[A3], Rep[A4]) => Rep[B] = {
    val g = (x1: Rep[A1], x2: Rep[A2], x3: Rep[A3], x4: Rep[A4]) => toplevelApply[B](name, List(x1, x2, x3, x4))
    rec.getOrElseUpdate(name, mkTopLevel(name, List(implicitly[Typ[A1]], implicitly[Typ[A2]], implicitly[Typ[A3]], implicitly[Typ[A4]]), implicitly[Typ[B]], xs => f(xs(0).asInstanceOf[Rep[A1]], xs(1).asInstanceOf[Rep[A2]], xs(2).asInstanceOf[Rep[A3]], xs(3).asInstanceOf[Rep[A4]]), xs => pre(xs(0).asInstanceOf[Rep[A1]], xs(1).asInstanceOf[Rep[A2]], xs(2).asInstanceOf[Rep[A3]], xs(3).asInstanceOf[Rep[A4]]), (xs: List[Rep[_]]) => (r: Rep[B]) => post(xs(0).asInstanceOf[Rep[A1]], xs(1).asInstanceOf[Rep[A2]], xs(2).asInstanceOf[Rep[A3]], xs(3).asInstanceOf[Rep[A4]])(r)))
    g
  }
  def toplevel[A1:Manifest:Typ,A2:Manifest:Typ,A3:Manifest:Typ,A4:Manifest:Typ,A5:Manifest:Typ,A6:Manifest:Typ,A7:Manifest:Typ,A8:Manifest:Typ,A9:Manifest:Typ,B:Manifest:Typ](name: String, f: (Rep[A1], Rep[A2], Rep[A3], Rep[A4], Rep[A5], Rep[A6], Rep[A7], Rep[A8], Rep[A9]) => Rep[B], pre: (Rep[A1], Rep[A2], Rep[A3], Rep[A4], Rep[A5], Rep[A6], Rep[A7], Rep[A8], Rep[A9]) => Rep[Boolean], post: (Rep[A1], Rep[A2], Rep[A3], Rep[A4], Rep[A5], Rep[A6], Rep[A7], Rep[A8], Rep[A9]) => Rep[B] => Rep[Boolean]): (Rep[A1], Rep[A2], Rep[A3], Rep[A4], Rep[A5], Rep[A6], Rep[A7], Rep[A8], Rep[A9]) => Rep[B] = {
    val g = (x1: Rep[A1], x2: Rep[A2], x3: Rep[A3], x4: Rep[A4], x5: Rep[A5], x6: Rep[A6], x7: Rep[A7], x8: Rep[A8], x9: Rep[A9]) => toplevelApply[B](name, List(x1, x2, x3, x4, x5, x6, x7, x8, x9))
    rec.getOrElseUpdate(name, mkTopLevel(name, List(implicitly[Typ[A1]], implicitly[Typ[A2]], implicitly[Typ[A3]], implicitly[Typ[A4]], implicitly[Typ[A5]], implicitly[Typ[A6]], implicitly[Typ[A7]], implicitly[Typ[A8]], implicitly[Typ[A9]]), implicitly[Typ[B]], xs => f(xs(0).asInstanceOf[Rep[A1]], xs(1).asInstanceOf[Rep[A2]], xs(2).asInstanceOf[Rep[A3]], xs(3).asInstanceOf[Rep[A4]], xs(4).asInstanceOf[Rep[A5]], xs(5).asInstanceOf[Rep[A6]], xs(6).asInstanceOf[Rep[A7]], xs(7).asInstanceOf[Rep[A8]], xs(8).asInstanceOf[Rep[A9]]), xs => pre(xs(0).asInstanceOf[Rep[A1]], xs(1).asInstanceOf[Rep[A2]], xs(2).asInstanceOf[Rep[A3]], xs(3).asInstanceOf[Rep[A4]], xs(4).asInstanceOf[Rep[A5]], xs(5).asInstanceOf[Rep[A6]], xs(6).asInstanceOf[Rep[A7]], xs(7).asInstanceOf[Rep[A8]], xs(8).asInstanceOf[Rep[A9]]), (xs: List[Rep[_]]) => (r: Rep[B]) => post(xs(0).asInstanceOf[Rep[A1]], xs(1).asInstanceOf[Rep[A2]], xs(2).asInstanceOf[Rep[A3]], xs(3).asInstanceOf[Rep[A4]], xs(4).asInstanceOf[Rep[A5]], xs(5).asInstanceOf[Rep[A6]], xs(6).asInstanceOf[Rep[A7]], xs(7).asInstanceOf[Rep[A8]], xs(8).asInstanceOf[Rep[A9]])(r)))
    g
  }

  implicit def anyTyp: Typ[Any]

  def valid[A](p: Rep[A]): Rep[Boolean]
  def valid[A](p: Rep[A], r: Rep[Any]): Rep[Boolean]
  def old[A:Typ](v: Rep[A]): Rep[A]

  def reflectMutableInput[A](v: Rep[A]): Rep[A]
  def reflectMutableInput[A](v: Rep[A], r: Rep[Range]): Rep[A]

  def assigns(s: Rep[Any]): Unit
  def assigns(s: Rep[Any], r: Rep[Any]): Unit

  def requires(c: => Rep[Boolean]): Unit
  def ensures[B:Typ](c: Rep[B] => Rep[Boolean]): Unit

  def toplevelApply[B:Typ](name: String, args: List[Rep[_]]): Rep[B]
  def exists[A:Typ](f: Rep[A] => Rep[Boolean]): Rep[Boolean]
  def forall[A:Typ](f: Rep[A] => Rep[Boolean]): Rep[Boolean]
  def infix_==>(a: Rep[Boolean], b: Rep[Boolean]): Rep[Boolean]
  def infix_within[A](p: Rep[Array[A]], r: Rep[Range]): Rep[Any]

  def loop(invariant: Rep[Int] => Rep[Boolean], assigns: Rep[Int] => Rep[List[Any]], variant: Rep[Int] => Rep[Int])(l: Rep[Unit]): Rep[Unit]
  def loop(invariant: => Rep[Boolean], assigns: => Rep[List[Any]], variant: => Rep[Int])(l: Rep[Unit]): Rep[Unit]

  def _assert(cond: =>Rep[Boolean])(implicit pos: SourceContext): Rep[Unit]

  def predicate[A:Manifest:Typ](name: String, f: Rep[A] => Rep[Boolean]): Rep[A] => Rep[Boolean] = {
    val g = (x: Rep[A]) => toplevelApply[Boolean](name, List(x))
    rec.getOrElseUpdate(name, TopLevel(name, List(implicitly[Typ[A]]), implicitly[Typ[Boolean]], xs => f(xs(0).asInstanceOf[Rep[A]]), spec=true))
    g
  }
}

trait VerifyOpsExp extends VerifyOps with EffectExp with RangeOpsExp with LiftBoolean with ListOpsExp with BooleanOpsExpOpt {
  implicit def anyTyp: Typ[Any] = manifestTyp

  var suspendCSE: Boolean = false
  def reifySpec[A:Typ](block: => Exp[A]): Block[A] = {
    val savedCSE = suspendCSE
    suspendCSE = true
    try {
      reifyEffects(block)
    } finally {
      suspendCSE = savedCSE
    }
  }

  override protected implicit def toAtom[T:Typ](d: Def[T])(implicit pos: SourceContext): Exp[T] = {
    def a = super.toAtom[T](d)(implicitly[Typ[T]], pos)
    if (suspendCSE) d match {
      case _:Reify[_] => a
      case _:Reflect[_] => a
      case _ => reflectEffect(d)
    } else a
  }

  case class Valid[A](p: Rep[A], r: Option[Rep[Any]]) extends Def[Boolean]
  case class Old[A](v: Rep[A]) extends Def[A]

  def valid[A](p: Rep[A]): Rep[Boolean] = Valid[A](p, None)
  def valid[A](p: Rep[A], r: Rep[Any]): Rep[Boolean] = Valid[A](p, Some(r))
  def old[A:Typ](v: Rep[A]): Rep[A] = Old[A](v)

  def reflectMutableInput[A](v: Rep[A]): Rep[A] =
    reflectMutableSym(v.asInstanceOf[Sym[A]])
  val inputs = new scala.collection.mutable.LinkedHashMap[Sym[Any], Rep[Range]]
  def reflectMutableInput[A](v: Rep[A], r: Rep[Range]): Rep[A] = {
    inputs += (v.asInstanceOf[Sym[Any]] -> r)
    reflectMutableInput(v)
  }

  case class Loc(s: Rep[Any], r: Option[Rep[Any]])
  var locs: List[Loc] = Nil
  def assigns(s: Rep[Any]): Unit = {
    locs = Loc(s, None)::locs
  }
  def assigns(s: Rep[Any], r: Rep[Any]): Unit = {
    locs = Loc(s, Some(r))::locs
  }

  case class Cond(t: () => Exp[Boolean])
  case class PostCond[A:Typ](t: Exp[A] => Exp[Boolean]) {
    def apply[B:Typ](r: Exp[B]) = {
      assert (implicitly[Typ[B]] == implicitly[Typ[A]])
      Cond{() => t(r.asInstanceOf[Exp[A]])}
    }
  }
  var preconds: List[Cond] = Nil
  def requires(c: => Rep[Boolean]): Unit = {
    preconds = Cond{() => c}::preconds
  }
  var postconds: List[PostCond[_]] = Nil
  def ensures[B:Typ](c: Rep[B] => Rep[Boolean]): Unit = {
    postconds = PostCond(c)::postconds
  }
  def reifyConds(cs: List[Cond]): Block[Boolean] = {
    reifySpec(cs.foldRight[Exp[Boolean]](unit(true)){case (Cond(t), b) => boolean_and(b, t())})
  }

  case class ToplevelApply[B:Typ](name: String, args: List[Rep[_]]) extends Def[B]
  val eff = new scala.collection.mutable.LinkedHashMap[String,(List[Sym[Any]], Summary)]
  def toplevelApply[B:Typ](name: String, args: List[Rep[_]]): Rep[B] = {
    eff.get(name) match {
      case Some((params,es)) =>
        val m = params.zip(args.map(_.asInstanceOf[Sym[Any]])).toMap
        val es2 = replaceInSummary(m, es)
        reflectEffect(ToplevelApply[B](name, args), es2)
      case None => reflectEffect(ToplevelApply[B](name, args))
    }
  }
  def replaceInSummary(m: Map[Sym[Any], Sym[Any]], es: Summary) = {
    def r1(x: Sym[Any]) = m.get(x) match {
      case Some(y) if y != null => y
      case None => x
    }
    def r(xs: List[Sym[Any]]) = xs.map(r1)
    Summary(es.maySimple, es.mstSimple, es.mayGlobal, es.mstGlobal, es.resAlloc, es.control, r(es.mayRead), r(es.mstRead), r(es.mayWrite), r(es.mstWrite))
  }

  case class Quantifier[A:Typ](k: String, x: Sym[A], y: Block[Boolean]) extends Def[Boolean]
  def quantifier[A:Typ](k: String, f: Rep[A] => Rep[Boolean]): Rep[Boolean] = {
    val x = fresh[A]
    val y = reifySpec(f(x))
    Quantifier(k, x, y)
  }
  def exists[A:Typ](f: Rep[A] => Rep[Boolean]): Rep[Boolean] =
    quantifier("\\exists", f)
  def forall[A:Typ](f: Rep[A] => Rep[Boolean]): Rep[Boolean] =
    quantifier("\\forall", f)
  case class Implies(a: Rep[Boolean], b: Rep[Boolean]) extends Def[Boolean]
  def infix_==>(a: Rep[Boolean], b: Rep[Boolean]): Rep[Boolean] = Implies(a, b)

  case class Within[A](p: Rep[Array[A]], r: Rep[Range]) extends Def[Any]
  def infix_within[A](p: Rep[Array[A]], r: Rep[Range]): Rep[Any] = Within[A](p, r)

  case class Loop(invariant: Block[Boolean], assigns: Block[List[Any]], variant: Block[Int]) extends Def[Unit]
  val loops = new scala.collection.mutable.LinkedHashMap[Sym[_], Loop]
  val loopsDone = new scala.collection.mutable.LinkedHashSet[Sym[_]]
  def loop(invariant: Rep[Int] => Rep[Boolean], assigns: Rep[Int] => Rep[List[Any]], variant: Rep[Int] => Rep[Int])(l: Rep[Unit]): Rep[Unit] = {
    val i = l match {
      case Def(Reflect(RangeForeach(_, _, i, _), _, _)) => i
    }
    loop(invariant(i), assigns(i), variant(i))(l)
  }
  def loop(invariant: => Rep[Boolean], assigns: => Rep[List[Any]], variant: => Rep[Int])(l: Rep[Unit]): Rep[Unit] = {
    val s = l.asInstanceOf[Sym[_]]
    val y1 = reifySpec(invariant)
    val y2 = reifySpec(assigns)
    val y3 = reifySpec(variant)
    val r = Loop(y1, y2, y3)
    loops.getOrElseUpdate(l.asInstanceOf[Sym[Unit]], r)
    l
  }

  case class Assert(y: Block[Boolean]) extends Def[Unit]
  def _assert(cond: =>Rep[Boolean])(implicit pos: SourceContext): Rep[Unit] = {
    val y = reifySpec(cond)
    val r = reflectEffect(Assert(y))
    r
  }

  override def syms(e: Any): List[Sym[Any]] = e match {
    case Assert(y) => syms(y)
    case Quantifier(k, x, y) => syms(y)
    case _ => super.syms(e)
  }

  override def boundSyms(e: Any): List[Sym[Any]] = e match {
    case Assert(y) => effectSyms(y)
    case Quantifier(k, x, y) => syms(x) ::: effectSyms(y)
    case _ => super.boundSyms(e)
  }

  override def symsFreq(e: Any): List[(Sym[Any], Double)] = e match {
    case Assert(y) => freqCold(y)
    case Quantifier(k, x, y) => freqCold(y)
    case _ => super.symsFreq(e)
  }

}

trait Dsl extends VerifyOps with ScalaOpsPkg with TupledFunctions with UncheckedOps with LiftPrimitives with LiftString with LiftVariables with LiftBoolean with LiftNumeric with ZeroVal {
  implicit def repStrToSeqOps(a: Rep[String]) = new SeqOpsCls(a.asInstanceOf[Rep[Seq[Char]]])
  override def infix_&&(lhs: Rep[Boolean], rhs: => Rep[Boolean])(implicit pos: SourceContext): Rep[Boolean] =
    __ifThenElse(lhs, rhs, unit(false))
}

trait Impl extends Dsl with VerifyOpsExp with ScalaOpsPkgExp with TupledFunctionsRecursiveExp with UncheckedOpsExp with ZeroValExp { self =>
  val codegen = new CCodeGenPkg with CGenVariables with CGenTupledFunctions with CGenUncheckedOps {
    var emitFileAndLine: Boolean = false
    override def quote(x: Exp[Any]) = x match {
      case Const(true) => "1/*true*/"
      case Const(false) => "0/*false*/"
      case Const(null) => "0/*null*/"
      case Const(c: Char) if c==0.toChar => "'\\0'"
      case Const(s: String) =>
        // undo CCodegen
        super.quote(x).stripPrefix("string(").stripSuffix(")")
      case _ => super.quote(x)
    }

    override def remap[A](m: Typ[A]): String = {
      val s = m.toString
      if (s.startsWith("Array["))
        return remap(m.typeArguments.head)+" "
      val tpe = super.remap(m)
      if (tpe.startsWith("int") || tpe.startsWith("uint") || tpe=="bool")
        if (s == "Char") "char" else "int"
      else if (tpe == "string") "char "
      else tpe
    }
    override def isPrimitiveType(tpe: String): Boolean = {
      tpe match {
        case "int" | "uint" | "char" => true
        case _ => super.isPrimitiveType(tpe)
      }
    }
    override def emitValDef(sym: Sym[Any], rhs: String): Unit = {
      if (!isVoidType(sym.tp)) super.emitValDef(sym, rhs)
      else emitVoid(rhs)
    }
    override def emitVarDef(sym: Sym[Variable[Any]], rhs: String): Unit = {
      if (!isVoidVar(sym)) super.emitVarDef(sym, rhs)
      else emitVoid(rhs)
    }
    override def emitAssignment(sym: Sym[Any], rhs: String): Unit = {
      if (!isVoidVar(sym)) super.emitAssignment(sym, rhs)
      else emitVoid(rhs)
    }
    def isVoidVar(sym: Sym[Any]) = isVoidType(sym.tp.typeArguments.head)
    def emitVoid(rhs: String): Unit = {
      if (rhs.contains("("))
        stream.println(rhs + ";")
    }

    val default_m =  Map[Sym[_], String]()
    val emitted = new scala.collection.mutable.LinkedHashSet[Sym[_]]
    def exprOfBlock[A](kw: String, e: Block[A], m: Map[Sym[_], String] = Map()): String = {
      val r = exprOfBlock(e, m)
      r match {
        case "true" => ""
        case _ => kw + " " + r + ";"
      }
    }
    def exprOfBlock[A](e: Block[A], m: Map[Sym[_], String]): String =
      exprOf(e.res, m)
    def exprOfDef[A](d: Def[A], m: Map[Sym[_], String]): String = d match {
      case ToplevelApply(name, es) => name+"("+es.map(exprOf(_, m)).mkString(",")+")"
      case Old(v) => "\\old("+exprOf(v, m)+")"
      case Valid(p, or) => or match {
        case None => "\\valid("+exprOf(p, m)+")"
        case Some(r) => "\\valid("+exprOf(p, m)+"+"+exprOf(r, m)+")"
      }
      case Until(a, b) => b match {
        case Const(c:Int) => "("+exprOf(a, m)+".."+(c-1)+")"
        case Def(IntPlus(c1, Const(1))) => "("+exprOf(a, m)+".."+exprOf(c1, m)+")"
        case _ => "("+exprOf(a, m)+".."+exprOf(b, m)+"-1)"
      }
      case Quantifier(k, x, y) => "("+k+" "+remapWithRef(x.tp)+" "+quote(x)+"; "+exprOfBlock(y, m)+")"
      case Implies(a, b) => "("+exprOf(a, m)+" ==> "+exprOf(b, m)+")"
      case Within(p, r) => exprOf(p, m)+"["+exprOf(r, m)+"]"
      case Equal(a, b) => "("+exprOf(a, m)+"=="+exprOf(b, m)+")"
      case NotEqual(a, b) => "("+exprOf(a, m)+"!="+exprOf(b, m)+")"
      case OrderingGTEQ(a, b) => "("+exprOf(a, m)+">="+exprOf(b, m)+")"
      case OrderingGT(a, b) => "("+exprOf(a, m)+">"+exprOf(b, m)+")"
      case OrderingLTEQ(a, b) => "("+exprOf(a, m)+"<="+exprOf(b, m)+")"
      case OrderingLT(a, b) => "("+exprOf(a, m)+"<"+exprOf(b, m)+")"
      case BooleanAnd(a, b) => "("+exprOf(a, m)+" && "+exprOf(b, m)+")"
      case IfThenElse(a, Block(Const(true)), Block(Const(false))) => exprOf(a, m)
      case IfThenElse(a, b, Block(Const(false))) => "("+exprOf(a, m)+" && "+exprOfBlock(b, m)+")"
      case BooleanOr(a, b) => "("+exprOf(a, m)+" || "+exprOf(b, m)+")"
      case BooleanNegate(a) => "(!"+exprOf(a, m)+")"
      case ObjIntMaxValue() => "INT_MAX"
      case ObjIntMinValue() => "INT_MIN"
      case IntPlus(a, b) => "("+exprOf(a, m)+"+"+exprOf(b, m)+")"
      case IntMinus(a, b) => "("+exprOf(a, m)+"-"+exprOf(b, m)+")"
      case IntTimes(a, b) => "("+exprOf(a, m)+"*"+exprOf(b, m)+")"
      case IntMod(a, b) => "("+exprOf(a, m)+"%"+exprOf(b, m)+")"
      case IntBinaryAnd(a, b) => "("+exprOf(a, m)+"&"+exprOf(b, m)+")"
      case ArrayApply(p, i) => exprOf(p, m)+"["+exprOf(i, m)+"]"
      case Reify(r, _, _) => exprOf(r, m)
      case Reflect(r, _, _) => exprOfDef(r, m)
      case ReadVar(Variable(s@Sym(n))) => if (isVoidVar(s)) "" else quote(s)
      case ListNew(xs) => xs.map(exprOf(_, m)).filter(_.nonEmpty).mkString(", ")
      // FIXME: only works for strings / Seq[Char] / Array[Char]
      case SeqLength(x) => "strlen("+exprOf(x, m)+")"
      case ArrayLength(x) => "strlen("+exprOf(x, m)+")"
      case _ => "TODO:Def:"+d
    }
    def exprOf[A](e: Exp[A], m: Map[Sym[_], String] = Map()): String = e match {
      case Const(b: Boolean) => b.toString
      case Const(_) => quote(e)
      case s@Sym(n) => s match {
        case Def(d) if !emitted(s) => exprOfDef(d, m)
        case _ => m.get(s) match {
          case Some(v) => v
          case None => quote(e)
        }
      }
    }

    override def emitNode(sym: Sym[Any], rhs: Def[Any]) = {
      emitted += sym
      if (emitFileAndLine && !rhs.isInstanceOf[Reflect[_]]) {
        val s = quotePos(sym).split("//")(0).split(":")
        stream.println(s"""#line ${s(1)} "${s(0)}" """)
      }
      rhs match {
        case ToplevelApply(name, args) => emitValDef(sym, name+args.map(quote).mkString("(", ",", ")"))
        case Assert(y) => stream.println(exprOfBlock("//@assert", y))
        case ArrayApply(x,n) => emitValDef(sym, quote(x) + "[" + quote(n) + "]")
        case ArrayUpdate(x,n,y) => stream.println(quote(x) + "[" + quote(n) + "] = " + quote(y) + ";")
        // TODO: the LMS C codegen should be updated to use emitAssignment instead
        case Assign(Variable(a), b) => emitAssignment(a.asInstanceOf[Sym[Variable[Any]]], quote(b))
        case SeqLength(x) =>
          // FIXME: only works for strings / Seq[Char]
          emitValDef(sym, "strlen("+quote(x)+")")
        case SeqApply(x,n) => emitValDef(sym, quote(x) + "[" + quote(n) + "]")
        case _ if loops.contains(sym) && !loopsDone.contains(sym) =>
          loopsDone += sym
          val Loop(invariant, assigns, variant) = loops.get(sym).get
          stream.println("/*@")
          stream.println(exprOfBlock("loop invariant", invariant))
          stream.println(exprOfBlock("loop assigns", assigns))
          if (!variant.res.isInstanceOf[Const[_]])
            stream.println(exprOfBlock("loop variant", variant))
          stream.println("*/")
          super.emitNode(sym, rhs)
        case RangeForeach(start, end, i, body) if !loopsDone.contains(sym) =>
          loopsDone += sym
          stream.println("/*@")
          stream.println("loop invariant 0<="+quote(i)+"<="+quote(end)+";")
          val s = summarizeEffects(body)
          val xs = for (x <- s.mayWrite) yield (inputs.get(x) match {
            case Some(r) => quote(x)+"["+exprOf(r)+"]"
            case None => quote(x)
          })
          stream.println("loop assigns "+quote(i)+(if (xs.isEmpty) "" else ", ")+xs.mkString(", ")+";")
          stream.println("loop variant "+quote(end)+"-"+quote(i)+";")
          stream.println("*/")
          super.emitNode(sym, rhs)
        case _ => super.emitNode(sym, rhs)
      }
    }

    def emitHeader[B](functionName: String, out: PrintWriter)(mAs: List[Typ[_]], mB: Typ[B]): Unit = {
      val args = mAs.map(fresh(_))
      val r = fresh[B](mB)
      val sB = remapWithRef(mB).trim()
      withStream(out) {
        stream.println(sB+" "+functionName+"("+(args.map(s => remapWithRef(s.tp)+" "+quote(s))).mkString(", ")+");")
      }
    }

    def emitVerify[B](f: List[Exp[_]] => Exp[B], functionName: String, spec: Boolean, out: PrintWriter)(mAs: List[Typ[_]], mB: Typ[B]): Unit = {
      val args = mAs.map(fresh(_))
      val r = fresh[B](mB)
      val oldFns = rec.keys.toSet
      val body = reifyBlock(f(args))(mB)
      val fns = rec.keys.toSet -- oldFns
      eff.getOrElseUpdate(functionName, (args, summarizeEffects(body)))
      val preBody = reifyConds(preconds)
      preconds = Nil
      val postBody = reifyConds(postconds.map{f => f(r)(mB)})
      postconds = Nil
      val sB = remapWithRef(mB).trim()
      val y = getBlockResult(body)
      val assignsNothing = autoAssignNothing && {
        val s = summarizeEffects(body)
        s.mayWrite.isEmpty && s.mstWrite.isEmpty
      }
      val customAssignsStr =
        if (!locs.isEmpty) {
          assert(!assignsNothing)
          locs.reverse.map{ case Loc(s, or) =>
            or match {
              case None => exprOf(s)
              case Some(r) => exprOf(s)+"["+exprOf(r)+"]"
            }
          }.mkString("assigns ", ", ", ";")
        } else ""
      locs = Nil // reset

      fns.foreach { case k =>
        val x = rec(k)
        emitHeader(x.name, out)(x.mAs, mtype(x.mB))
      }
      val sig = functionName+"("+(args.map(s => remapWithRef(s.tp)+" "+quote(s))).mkString(", ")+")"
      val sig_app = functionName+"("+(args.map(s => quote(s))).mkString(", ")+")"
      withStream(out) {
        if (spec) {
          stream.println("//@ predicate "+sig+" = "+exprOfBlock[B](body, default_m)+";")
        }
        val preStr = exprOfBlock("requires", preBody)
        val postStr = exprOfBlock("ensures", postBody, Map(r -> "\\result"))
        if (!preStr.isEmpty || assignsNothing || !customAssignsStr.isEmpty || !postStr.isEmpty || spec) {
          stream.println("/*@")
          stream.println(preStr)
          if (assignsNothing) stream.println("assigns \\nothing;")
          stream.println(postStr)
          if (spec) {
            stream.println("ensures \\result <==> "+sig_app+";")
          }
          if (!customAssignsStr.isEmpty) stream.println(customAssignsStr)
          stream.println("*/")
        }

        stream.println(sB+" "+sig+" {")
        emitBlock(body)

        if (remap(y.tp) != "void")
          stream.println("return " + quote(y) + ";")

        stream.println("}")
      }
    }
    val IR: self.type = self
  }
  def emitAll(stream: java.io.PrintWriter): Unit = {
    assert(codegen ne null) //careful about initialization order
    includes.foreach { i => stream.println("#include "+i) }
    rec.foreach { case (k,x) =>
      codegen.emitVerify(x.f, x.name, x.spec, stream)(x.mAs, mtype(x.mB))
    }
  }
  lazy val code: String = {
    val source = new java.io.StringWriter()
    val stream = new PrintWriter(source)
    emitAll(stream)
    source.toString
  }
}
