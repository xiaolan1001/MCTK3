package edu.wis.jtlv.env.module;

import net.sf.javabdd.BDDDomain;
import net.sf.javabdd.BDDException;
import net.sf.javabdd.BDDVarSet;

/**
 * <p>
 * JTLVBDDField is an object representing a field variable in JTLV environment.
 * On one hand, this object encapsulate the BDD domain, which does not
 * necessarily has two boolean values. From the other hand, this object also
 * encapsulate both prime and unprime, versions of the variables.
 * </p>
 * 
 * @version {@value edu.wis.jtlv.env.Env#version}
 * @author yaniv sa'ar.
 * 
 */
public class ModuleBDDField extends ModuleEntity {
	/**
	 * <p>
	 * The domain of this field.
	 * </p>
	 */
	protected BDDDomain main;

	/**
	 * <p>
	 * Identify whether this field is the prime or the unprime version of the
	 * field.
	 * </p>
	 */
	protected boolean is_prime;

	/**
	 * <p>
	 * The other version of this field. i.e., if this is the unprime version
	 * then this.pair is the prime one, and wise versa.
	 * </p>
	 */
	protected ModuleBDDField pair;

	/**
	 * <p>
	 * The main public constructor for JTLVBDDField. Given a path, a name, a
	 * domain, and a corresponding domain, a new BDD field is created with a
	 * corresponding prime version of the field.
	 * </p>
	 * 
	 * @param unprime
	 *            The domain to which we are constructing a field.
	 * @param prime
	 *            The other corresponding domain.
	 * @param a_path
	 *            A path to the field.
	 * @param name
	 *            A name for this field.
	 * 
	 * @see edu.wis.jtlv.env.Env#newVar(String, String)
	 * @see edu.wis.jtlv.env.Env#newVar(String, String, int)
	 */
	public ModuleBDDField(BDDDomain unprime, BDDDomain prime, String a_path,
			String name) {
		this.main = unprime;
		this.main.setName(name);
		this.path = a_path;
		this.name = name;
		this.is_prime = false;
		this.pair = new ModuleBDDField(prime, this, a_path, name + "'");
	}

	/**
	 * <p>
	 * The corresponding private constructor for creating the prime version of
	 * the field.
	 * </p>
	 * 
	 * @param prime
	 *            The domain to which we are constructing a field.
	 * @param main_pair
	 *            The other JTLVBDDField which has invoked this instance.
	 * @param a_path
	 *            A path to the field.
	 * @param name
	 *            A name for this field.
	 *
	 */
	private ModuleBDDField(BDDDomain prime, ModuleBDDField main_pair,
			String a_path, String name) {
		this.main = prime;
		this.main.setName(name);
		this.path = a_path;
		this.name = name;
		this.is_prime = true;
		this.pair = main_pair;
	}

	//LXY: for creating unprime only variables, including input and action variables
	public ModuleBDDField(BDDDomain unprime_only, String a_path, String name) {
		this.main = unprime_only;
		this.main.setName(name);
		this.path = a_path;
		this.name = name;
		this.is_prime = false;
		this.pair = null;
	}

	/**
	 * <p>
	 * Return the other version of the field, regardless of which instance this
	 * is.
	 * </p>
	 * 
	 * @return The other version of the field.
	 * 
	 * @see edu.wis.jtlv.env.module.ModuleBDDField#prime()
	 * @see edu.wis.jtlv.env.module.ModuleBDDField#unprime()
	 */
	public ModuleBDDField other() {
		return this.pair;
	}

	/**
	 * <p>
	 * Get the prime version of this field.
	 * </p>
	 * 
	 * @return The prime version of this field.
	 * @throws BDDException
	 *             If this is a prime version of the field.
	 * 
	 * @see edu.wis.jtlv.env.module.ModuleBDDField#other()
	 * @see edu.wis.jtlv.env.module.ModuleBDDField#unprime()
	 */
	public ModuleBDDField prime() throws BDDException {
		if (is_prime) {
			throw new BDDException("Cannot prime primed variables.");
		}
		if (this.other()==null)
			throw new BDDException(this.getPath()+"."+this.getName()+" is an input or action variable that without prime version.");

		return this.other();
	}

	/**
	 * <p>
	 * Get the unprime version of this field.
	 * </p>
	 * 
	 * @return The unprime version of this field.
	 * @throws BDDException
	 *             If this is an unprime version of the field.
	 * 
	 * @see edu.wis.jtlv.env.module.ModuleBDDField#other()
	 * @see edu.wis.jtlv.env.module.ModuleBDDField#prime()
	 */
	public ModuleBDDField unprime() throws BDDException {
		if (!is_prime) {
			throw new BDDException("Cannot unprime unprimed variables.");
		}
		return this.other();
	}

	/**
	 * <p>
	 * Get the set of BDD variables which construct the domain for this field.
	 * </p>
	 * 
	 * @return The set of BDD variables.
	 */
	public BDDVarSet support() {
		// return this.getDomain().set();
		return this.getDomain().ithVar(0).support();
	}

	/**
	 * <p>
	 * Check whether this is a prime version of the field representation.
	 * </p>
	 * 
	 * @return true if this is the prime version of the field, false otherwise.
	 */
	public boolean isPrime() {
		return is_prime;
	}

	/**
	 * <p>
	 * Getter for the domain of this field.
	 * </p>
	 * 
	 * @return The domain of this field.
	 * 
	 * @see edu.wis.jtlv.env.module.ModuleBDDField#getOtherDomain()
	 */
	public BDDDomain getDomain() {
		return this.main;
	}

	/**
	 * <p>
	 * Getter for the domain of the other corresponding field.
	 * </p>
	 * 
	 * @return The domain of the other corresponding field.
	 * 
	 * @see edu.wis.jtlv.env.module.ModuleBDDField#getDomain()
	 */
	public BDDDomain getOtherDomain() {
		if (this.other()==null) { //by LXY
			throw new BDDException("The other domain of variable "+this.path+"."+this.name + " is null.");
		}
		return this.other().getDomain();
	}

	/**
	 * <p>
	 * Check whether this object's domain is comparable to the give object
	 * domain.
	 * </p>
	 * 
	 * @param other
	 *            The other object to compare this filed to.
	 * @return true if the given object's domain is comparable to this domain,
	 *         false otherwise.
	 * 
	 * @see edu.wis.jtlv.env.module.ModuleBDDField#equals(Object)
	 * @see edu.wis.jtlv.env.module.ModuleBDDField#strongEquals(Object)
	 */
	public boolean comparable(ModuleBDDField other) {
		return this.getDomain().size().equals((other.getDomain().size()));
	}

}
