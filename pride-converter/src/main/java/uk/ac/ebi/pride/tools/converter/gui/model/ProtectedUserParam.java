package uk.ac.ebi.pride.tools.converter.gui.model;

import uk.ac.ebi.pride.tools.converter.report.model.UserParam;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 16/03/12
 * Time: 13:51
 */
public class ProtectedUserParam extends UserParam {

    public ProtectedUserParam() {
    }

    public ProtectedUserParam(String name, String value) {
        super(name, value);
    }

    public ProtectedUserParam(UserParam param) {
        super((param != null ? param.getName() : null), (param != null ? param.getValue() : null));
    }

}
