(function($) {
    Drupal.behaviors.yarr = {
        attach:
                function() {
                    $.getScript("http://java.com/js/dtjava.js",function(){dtjava.addOnloadCallback(javafxEmbed);});
    		    
                }

    }
}(jQuery));

function launchApplication(jnlpfile) {
    dtjava.launch({
        url: 'http://www.javaperformancetuning.co.za/sites/default/files/GCExplorer/GCExplorer.jnlp'
    },
    {
        javafx: '2.2+'
    },
    {}
    );
    return false;
}

function javafxEmbed() {
    dtjava.embed(
            {
                url: 'http://www.javaperformancetuning.co.za/sites/default/files/GCExplorer/GCExplorer.jnlp',
                placeholder: 'javafx-app-placeholder',
                width: 1300,
                height: 800
            },
    {
        javafx: '2.2+'
    },
    {}
    );
}

