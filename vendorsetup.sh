devices=('angler' 'angler_4core' 'beryllium' 'bonito' 'mata' 'oneplus3' 'sargo' 'taimen')

function lunch_devices() {
    add_lunch_combo statix_${device}-user
    add_lunch_combo statix_${device}-userdebug
}

for device in ${devices[@]}; do
    lunch_devices
done
