package com.zebra.scanner.inventory.bus;

import com.squareup.otto.Bus;

/**
 * Created by harshas on 3/20/17.
 */
public class BusProvider {
    /**
     * Maintains a singleton instance for obtaining the bus. Ideally this would be replaced with a more efficient means
     * such as through injection directly into interested classes.
     */

    private static final Bus BUS = new MainThreadBus();


    private BusProvider() {
        // No instances.
    }

    public static Bus getInstance() {
        return BUS;
    }


}
