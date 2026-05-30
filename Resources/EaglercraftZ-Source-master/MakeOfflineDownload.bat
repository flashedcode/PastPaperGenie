@echo off
title MakeOfflineDownload
java -cp "desktopRuntime/MakeOfflineDownload.jar;desktopRuntime/CompileEPK.jar" net.lax1dude.eaglercraft.v1_8.buildtools.workspace.MakeOfflineDownload "javascript/OfflineDownloadTemplate.txt" "javascript/classes.js" "javascript/assets.epk" "javascript/EaglercraftZ_1.20_Offline_en_US.html" "javascript/EaglercraftZ_1.20_Offline_International.html" "javascript/lang"
pause