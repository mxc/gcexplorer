#!/bin/sh

echo "=============================================="
echo " Welcome to GCExplorer JavaFX Packaging Script"
echo "=============================================="
echo "Runing javafxpackager ..."
echo "signing jars ..."

javafxpackager -signjar -keystore jumpingbean.jks -alias release -outdir build/signed -srcdir build/libs -verbose -storepass "$1"

if  [ $? -ne 0 ]; then 
	echo "Error signing jars"	
	exit
fi

echo "creating deployment bundles ..."
javafxpackager -deploy -srcdir build/signed -outdir build/dist -outfile GCExplorer -title GCExplorer -appclass za.co.jumpingbean.gcexplorer.ui.GCExplorer -vendor "Jumping Bean" -description "GCExplorer lets users explore JVM memory management" -preloader za.co.jumpingbean.gcexplorer.preloader.GCExplorerSplashScreen -allpermissions -width 1320 -height 800 -name GCExplorer 
