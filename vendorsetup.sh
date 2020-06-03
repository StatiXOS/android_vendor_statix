devices=('angler' 'angler_4core' 'ginkgo' 'evert' 'fajita' 'mata' 'oneplus3' )

function lunch_devices() {
    add_lunch_combo statix_${device}-user
    add_lunch_combo statix_${device}-userdebug
}

for device in ${devices[@]}; do
    lunch_devices
done
