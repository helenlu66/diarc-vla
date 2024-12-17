package edu.tufts.hrilab.vla.gui;

import ai.thinkingrobots.trade.TRADE;
import ai.thinkingrobots.trade.TRADEException;
import ai.thinkingrobots.trade.TRADEServiceConstraints;
import com.google.gson.JsonObject;
import edu.tufts.hrilab.belief.common.MemoryLevel;
import edu.tufts.hrilab.fol.Factory;
import edu.tufts.hrilab.fol.Predicate;
import edu.tufts.hrilab.fol.Term;
import edu.tufts.hrilab.gui.GuiAdapter;
import java.util.Collection;

public class VLAAdapter extends GuiAdapter {

    //==========================================================================
    // Constructors
    //==========================================================================
    public VLAAdapter(Collection<String> groups) {
        super(groups);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public String getPathRoot() {
        return "vla";
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected boolean providesTradeServices() {
        return false;
    }
}
