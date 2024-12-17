/*
 * Copyright © Thinking Robots, Inc., Tufts University, and others 2024.
 */
package edu.tufts.hrilab.config.project;

import edu.tufts.hrilab.diarc.DiarcConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// the Project config
public class ProjectConfig extends DiarcConfiguration {
    // for logging
    protected static Logger log = LoggerFactory.getLogger(ProjectConfig.class);

    // start the configuration
    @Override
    public void runConfiguration() {

        // create the sim output
        createInstance(edu.tufts.hrilab.simspeech.SimSpeechProductionComponent.class);

        createInstance(edu.tufts.hrilab.slug.listen.ListenerComponent.class);

        createInstance(edu.tufts.hrilab.slug.pragmatics.PragmaticsComponent.class, "-pragrules demos.prag");

        createInstance(edu.tufts.hrilab.slug.refResolution.ReferenceResolutionComponent.class);

        createInstance(edu.tufts.hrilab.slug.dialogue.DialogueComponent.class);

        createInstance(edu.tufts.hrilab.slug.nlg.SimpleNLGComponent.class);

        String gmArgs = "-beliefinitfile demos.pl agents/projectagents.pl " +
                "-asl core.asl dialogue/nlg.asl dialogue/handleSemantics.asl dialogue/nlu.asl " +
                "-goal listen(self)";
        createInstance(edu.tufts.hrilab.action.GoalManagerComponent.class, gmArgs);

        createInstance(edu.tufts.hrilab.vla.VLAComponent.class);
    }
}
