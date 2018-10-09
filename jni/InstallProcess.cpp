#include <jni.h>
#include <sys/types.h>
#include <sys/ioctl.h>
#include <time.h>
#include <sys/times.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <netdb.h>
#include <sys/stat.h>
#include <signal.h>
#include <pthread.h>




int main(int argc, char **argv) {

	system("chown root:root /data/canbot/openvpn/*;chmod 777 /data/canbot/openvpn/openvpn;chmod 666 /data/canbot/openvpn/c*");
	printf("install openvpn\n");
	system("cp /data/canbot/openvpn/openvpn /system/bin/openvpn");
	system(
			"mkdir /etc/openvpn && cp /data/canbot/openvpn/client.conf /etc/openvpn;"
					"cp /data/canbot/openvpn/client.crt /etc/openvpn;"
					"cp /data/canbot/openvpn/client.key /etc/openvpn;"
					"cp /data/canbot/openvpn/ca.crt /etc/openvpn");

	system(
			"chown root:root /data/canbot/dropbear/*;chmod 777 /data/canbot/dropbear/*");
	printf("install dropbear\n");
	system(
			"cp /data/canbot/dropbear/dropbear* /system/bin/;cp /data/canbot/dropbear/s* /system/bin/");
	printf("install end.......\n");

	return 0;
}
