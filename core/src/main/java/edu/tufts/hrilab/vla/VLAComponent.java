package edu.tufts.hrilab.vla;

import edu.tufts.hrilab.diarc.DiarcComponent;
import edu.tufts.hrilab.gui.GuiProvider;
import edu.tufts.hrilab.vla.gui.VLAAdapter;
import edu.tufts.hrilab.vla.util.Util;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VLAComponent extends DiarcComponent implements GuiProvider {
    // Class-level fields to store beliefs
    private Map<String, String> symbolicBeliefs;
    private Map<String, String> actionBeliefs;

    public VLAComponent() {
        this.symbolicBeliefs = new HashMap<>();
        this.actionBeliefs = new HashMap<>();
    }

    //==========================================================================
    // Implement methods | GuiProvider
    //==========================================================================
    @Nonnull
    @Override
    public String[] getAdapterClassNames() {
        return new String[]{
                VLAAdapter.class.getName()
        };
    }

    /**
     * Returns True if beliefs were successfully updated and False otherwise
     */
    public boolean updateBeliefs(ArrayList<Integer> objectRelations, ArrayList<Integer> actionStates) {
        if (objectRelations == null || actionStates == null) {
            return false;
        }

        try {
            // Update symbolic states
            String[] symbolicStates = Util.getSymbolicStates();
            Map<String, Integer> symbolicStateMap = new HashMap<>();
            for (int i = 0; i < objectRelations.size() && i < symbolicStates.length; i++) {
                Integer relation = objectRelations.get(i);
                String state = symbolicStates[i];
                symbolicStateMap.put(state, relation);
            }

            // Update action states
            String[] actionStateArray = Util.getActionStates();
            Map<String, Integer> actionStateMap = new HashMap<>();
            for (int j = 0; j < actionStates.size() && j < actionStateArray.length; j++) {
                Integer state = actionStates.get(j);
                String stateString = actionStateArray[j];
                actionStateMap.put(stateString, state);
            }

            // Update belief maps
            this.symbolicBeliefs = Util.reformatInputData(symbolicStateMap);
            this.actionBeliefs = Util.reformatInputData(actionStateMap);

            return true;
        } catch (Exception e) {
            // Log error or handle appropriately
            return false;
        }
    }

    /**
     * Returns an array of symbolic functions (ex. rightof(obj3, obj4))
     */
    public List<String> retrieveSymbolicBeliefs() {
        return new ArrayList<>(symbolicBeliefs.values());
    }

    /**
     * Returns an array of action functions (ex. grasping(obj2))
     */
    public List<String> retrieveActionBeliefs() {
        return new ArrayList<>(actionBeliefs.values());
    }
}