package com.mysoft.debug_tools.ui.connector;

import java.io.Serializable;

public interface EditCallback extends Serializable {
    void onValueChanged(String value);
}
