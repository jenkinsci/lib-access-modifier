package org.kohsuke.accmod.impl;

public class RestrictedElementImpl implements RestrictedElement {
    private final boolean isInTheInspectedModule;
    private final String keyName;

    public RestrictedElementImpl(boolean isInTheInspectedModule, String keyName) {
        this.isInTheInspectedModule = isInTheInspectedModule;
        this.keyName = keyName;
    }

    public boolean isInTheInspectedModule() {
        return isInTheInspectedModule;
    }

    @Override
    public boolean isSameClass(Location location) {
        String locationClassName = location.getClassName();
        if (locationClassName.contains("$")) {
            locationClassName = locationClassName.split("\\$")[0];
        }
        if(keyToClassName(keyName).equals(locationClassName)) {
            return true;
        }
        return false;
    }

    private static String keyToClassName(String key) {
        if (key.contains(".")) {
            key = key.split("\\.")[0];
        }
        return key.replace('/', '.');
    }


    public String toString() {
        return keyName;
    }
}
