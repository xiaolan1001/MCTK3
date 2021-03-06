package edu.wis.jtlv.env.core.smv.schema;

import edu.wis.jtlv.env.core.smv.SMVParseException;
import edu.wis.jtlv.env.module.SMVModule;

import java.util.Iterator;
import java.util.Vector;

import static edu.wis.jtlv.env.core.smv.schema.SMVAbstractElementInfo.SMVElementCategory.NULL;
import static edu.wis.jtlv.env.core.smv.schema.SMVAbstractElementInfo.SMVElementCategory.STATE_VAR;

public class SMVModuleInfo extends SMVContainerElementInfo {
	// not to confuse!!! if declared "p : process some_module();"
	// this.module_name is the module name "some_module" and not "p".
	public String[] arg_list;								//the inputted argument list: {"arg1","arg2",...}
	public boolean[] arg_visible_list;
	public SMVAbstractElementInfo[] arg_elememts;			//the reformatted inputted argument list: {arg1,arg2,...}
	private Vector<SMVAbstractElementInfo> sub_elements;	//the final argument list exactly used after then

	//LXY: for MAS
	public boolean isAgent;				// True: this module is an agent; False: this module is a normal module

	public SMVModuleInfo(boolean isAgent, String a_module_name, SMVParsingInfo an_info,
						 String[] an_arg_list, boolean[] an_arg_visible_list) throws SMVParseException {
		super(NULL,false, a_module_name, an_info);
		this.sub_elements = new Vector<SMVAbstractElementInfo>(20);

		this.arg_list = an_arg_list;
		this.arg_visible_list = an_arg_visible_list;

		//LXY: for MAS
		this.isAgent = isAgent;

		this.arg_elememts = new SMVAbstractElementInfo[this.arg_list.length];
		for (int i = 0; i < this.arg_elememts.length; i++) {
			this.arg_elememts[i] = null;
		}
	}

	public void add_element(SMVAbstractElementInfo elem, SMVParsingInfo an_info)
			throws SMVParseException {
		// first checking if it is declared as an argument.
		// (and is not defined already).
		for (int i = 0; i < this.arg_list.length; i++) {
			if (elem.name.equals(this.arg_list[i])) {
				if (this.arg_elememts[i] == null) {
					// "this" won't be the real holder here.
					this.arg_elememts[i] = elem;
					//elem.set_holder(this.get_holder());
					//this.get_holder().add_element(elem, an_info);
					return;
				}
				// Env.do_error(new SMVParseException("Variable " + elem.name
				// + " already exist in MODULE " + this.name, parse_info));
				// Env.do_parsing_error(an_info, new ModuleException(
				// "Variable " + elem.name
				// + " already exist in MODULE " + this.name));
				throw new SMVParseException("Variable " + elem.name
						+ " already exist in MODULE " + this.name, an_info);
			}
		}

		// then check that it does not exists in simple elements
		for (Iterator<SMVAbstractElementInfo> iter_elem = this.sub_elements
				.iterator(); iter_elem.hasNext();) {
			if (elem.name.equals(iter_elem.next().name)) {
				// will catch, issue an error, and try to recover...
				// Env.do_error(new SMVParseException("Variable " + elem.name
				// + " already exist in MODULE " + this.name, parse_info));
				// Env.do_parsing_error(an_info, new ModuleException("Variable "
				// + elem.name + " already exist in MODULE " + this.name));
				throw new SMVParseException("Variable " + elem.name
						+ " already exist in MODULE " + this.name, an_info);
				// return;
			}
		}

		// only now adding to the elements...
		elem.set_holder(this);
		this.sub_elements.add(elem);
	}

	public SMVAbstractElementInfo[] get_arg_elements() {
		return this.arg_elememts;
	}

	public SMVAbstractElementInfo get_element_named(String name) {
		for (Iterator<SMVAbstractElementInfo> iter_elem = this.sub_elements
				.iterator(); iter_elem.hasNext();) {
			SMVAbstractElementInfo elem = iter_elem.next();
			if (elem.name.equals(iter_elem.next().name)) {
				return elem;
			}
		}
		return null;
	}

	@Override
	public SMVAbstractElementInfo clone_element() throws SMVParseException {
		throw new SMVParseException("Cannot clone a MODULE.");
	}

	@Override
	public String typeString() {
		String res = "<type MODULE ";
		res += this.name + "(";
		for (int i = 0; i < this.arg_list.length; i++) {
			res += (i == 0) ? "" : ", ";
			res += this.arg_list;
		}
		return res + ")>";
	}

	@Override
	public void mk_fix_names() throws SMVParseException {
		// since sub_elements could change by this recursion, I need to do it in
		// such a manner.
		int curr_pos = 0;
		while (curr_pos < this.sub_elements.size()) {
			this.sub_elements.elementAt(curr_pos).mk_fix_names();
			curr_pos++;
		}
	}

	@Override
	public void mk_modules_skel(SMVModule self) throws SMVParseException {
		for (Iterator<SMVAbstractElementInfo> iter_elem = this.sub_elements
				.iterator(); iter_elem.hasNext();) {
			SMVAbstractElementInfo elem = iter_elem.next();
			elem.mk_modules_skel(self);
		}
	}

	@Override
	public void mk_defines(SMVModule self) throws SMVParseException {
		for (Iterator<SMVAbstractElementInfo> iter_elem = this.sub_elements
				.iterator(); iter_elem.hasNext();) {
			SMVAbstractElementInfo elem = iter_elem.next();
			elem.mk_defines(self);
		}
	}

	@Override
	public void mk_variables(SMVModule self) throws SMVParseException {
		for (Iterator<SMVAbstractElementInfo> iter_elem = this.sub_elements
				.iterator(); iter_elem.hasNext();) {
			SMVAbstractElementInfo elem = iter_elem.next();

			//LXY: for MAS
			// cannot declare ACT or ACTION within VAR declaration
			if (elem.category==STATE_VAR) {
				if (elem.name.equals("ACT"))
					throw new SMVParseException("Cannot take \'ACT\' as the name of a state variable, because it is " +
							"preserved as the action variable name of an agent.");
			}
			elem.mk_variables(self);
		}
	}

	@Override
	public void mk_module_args(SMVModule self) throws SMVParseException {
		for (Iterator<SMVAbstractElementInfo> iter_elem = this.sub_elements
				.iterator(); iter_elem.hasNext();) {
			SMVAbstractElementInfo elem = iter_elem.next();
			elem.mk_module_args(self);
		}
	}

	public Vector<SMVAbstractElementInfo> getSub_elements() {
		return sub_elements;
	}

}
