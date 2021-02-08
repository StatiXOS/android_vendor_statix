devices=( 'davinci' 'evert' 'fajita' 'ginkgo' 'guacamole' 'hotdogb' 'mata' 'oneplus3' 'TP1803' )

function lunch_devices() {
    add_lunch_combo statix_${device}-user
    add_lunch_combo statix_${device}-userdebug
}

for device in ${devices[@]}; do
    lunch_devices
done
