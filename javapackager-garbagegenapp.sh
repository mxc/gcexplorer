#!/bin/sh

echo "=============================================="
echo " Welcome to GCExplorer JavaFX Packaging Script"
echo "=============================================="
echo "Runing javafxpackager ..."
echo "signing jars ..."

currdir=`pwd`

cd $2/GarbageGeneratorApp/build/libs/

jarsigner -storepass "$1" -keystore $2/GCExplorerUI/jumpingbean.jks GarbageGeneratorApp.jar release

if  [ $? -ne 0 ]; then 
	echo "Error signing jars"
	cd $currdir
	exit
fi

cd $currdir

echo "creating deployment bundles ..."
javafxpackager -deploy -srcdir build/libs -outdir build/dist -outfile GarbageGeneratorApp -title GarbageGeneratorApp -appclass za.co.jumpingbean.gc.testapp.GarbageGeneratorApp -vendor "Jumping Bean" -description "Garbage generator application" -allpermissions -width 10 -height 10 -name GarbageGeneratorApp 
