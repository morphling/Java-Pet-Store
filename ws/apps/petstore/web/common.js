    
    function printDebug(argx) {
        if (typeof debug != 'undefined') {
            document.getElementById("status").innerHTML = argx  + "<br\>" + document.getElementById("status").innerHTML;
        }
    }


    function Map() {
        var size = 0;
        var keys = [];
        var values = [];
        
        this.put = function(key, value, replace) {
            if (this.get(key) == null) {
                keys[size] = key; 
                values[size] = value;
                size++;
            } else if (replace) {
                for (i=0; i < size; i++) {
                    if (keys[i] == key) {
                        values[i] = value;
                    }
                }
            }
        }
        
        this.get = function(key) {
            for (i=0; i < size; i++) {
                if (keys[i] == key) {
                    return values[i];
                }
            }
            return null;
        }
        
        this.clear = function() {
            size = 0;
            keys = [];
            values = [];
        }

        // return keys show can show main image
        this.getKeys = function() {
            return keys;
        }

        // dump contents of map keys as string
        this.contents = function() {
            var retx="";
            for(ii=0; ii < size; ii++) {
                retx += keys[ii] + ", ";
            }
            return retx;
        }

    }