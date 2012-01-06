package uk.ac.ebi.pride.tools.converter.gui;

import uk.ac.ebi.pride.tools.filter.PrideFilter;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 18/08/11
 * Time: 09:26
 */
public class FilterApplicationSelector {
    public static void main(String[] args) {
        if (args.length == 0) {
            FilterGUI.main(args);
        } else {
            PrideFilter.main(args);
        }
    }
}
