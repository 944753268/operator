#!/system/bin/sh

mount -o remount /system
############################删除运行目录###############################
rm -r /data/canbot

############################删除多个android apk###############################
#######adb uninstall com.canbot.operator
#pm uninstall com.canbot.operator
pm uninstall com.canbot.dirclient
pm uninstall com.canbot.statusclient
pm uninstall com.canbot.advertclient
pm uninstall com.canbot.logclient
#pm uninstall com.canbot.archiveclient
pm uninstall com.canbot.newsclient
pm uninstall com.canbot.timeaxisclient


#####kill存在的c程序####
busybox killall checkstatus
busybox killall collectlog
busybox killall readsqlite

############################删除openvpn与dropbear运行环境###############################
rm /system/bin/openvpn
rm -r /system/bin/bb
rm -r /etc/openvpn
busybox killall openvpn

rm /system/xbin/dropbear*
rm /system/xbin/scp
rm /system/xbin/ssh

rm -rf /system/xbin/bb
rm -rf /data/dropbear
busybox killall dropbear

###删除root帐号###
rm /etc/passwd
rm /etc/shadow
rm /etc/group
rm /etc/gshadow
rm /etc/shells
rm /etc/profile

###################################删除广告目录#####################################
rm -r /sdcard/canbot/advert
rm /sdcard/canbot/IdCheck
rm /sdcard/canbot/bin/*
#rm -r /sdcard/canbot/log

###################################重新安装apk#####################################
#sleep 3
#pm uninstall com.canbot.operator
#sleep 3
#pm install /sdcard/canbot/download/Operator.apk

