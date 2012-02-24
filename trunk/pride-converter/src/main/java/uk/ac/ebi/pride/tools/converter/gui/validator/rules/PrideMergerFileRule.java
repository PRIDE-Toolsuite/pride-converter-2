package uk.ac.ebi.pride.tools.converter.gui.validator.rules;

import psidev.psi.tools.validator.rules.Rule;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 24/02/12
 * Time: 11:06
 */
public class PrideMergerFileRule implements Rule {

    @Override
    public String getId() {
        return "MERGER";
    }

    @Override
    public String getName() {
        return "Merger File Number";
    }

    @Override
    public String getDescription() {
        return "You must provide at least two files to merge";
    }

    @Override
    public Collection<String> getHowToFixTips() {
        return null;
    }

}
