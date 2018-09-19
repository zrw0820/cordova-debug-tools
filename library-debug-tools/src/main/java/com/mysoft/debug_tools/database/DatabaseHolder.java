package com.mysoft.debug_tools.database;

import com.mysoft.debug_tools.database.protocol.IDescriptor;
import com.mysoft.debug_tools.database.protocol.IDriver;


/**
 * Created by linjiang on 29/05/2018.
 */

public class DatabaseHolder {
    public IDescriptor descriptor;
    public IDriver<? extends IDescriptor> driver;

    public DatabaseHolder(IDescriptor descriptor, IDriver<? extends IDescriptor> driver) {
        this.descriptor = descriptor;
        this.driver = driver;
    }
}
