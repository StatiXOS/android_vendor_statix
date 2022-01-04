devices=( 'beryllium' 'cannon' 'fajita' 'gauguin' 'ginkgo' 'guacamole' 'guacamoleb' 'hotdog' 'hotdogb' 'mata' 'sunfish' 'sweet' 'taimen' 'TP1803' 'walleye' )

function lunch_devices() {
    add_lunch_combo statix_${device}-user
    add_lunch_combo statix_${device}-userdebug
}

for device in ${devices[@]}; do
    lunch_devices
done
