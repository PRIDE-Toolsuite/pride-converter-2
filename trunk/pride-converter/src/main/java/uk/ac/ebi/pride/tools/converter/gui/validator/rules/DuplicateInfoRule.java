package uk.ac.ebi.pride.tools.converter.gui.validator.rules;

import psidev.psi.tools.validator.rules.Rule;

import java.util.Collection;

public class DuplicateInfoRule implements Rule {

        @Override
        public String getId() {
            return "Duplicate Experiment Information";
        }

        @Override
        public String getName() {
            return "Duplicate Experiment Information";
        }

        @Override
        public String getDescription() {
            return "Duplicate Experiment Information";
        }

        @Override
        public Collection<String> getHowToFixTips() {
            return null;
        }

        @Override
        public String toString() {
            return "Duplicate Experiment Information";
        }
    }
