devices=( 'cannon' 'guacamoleb' 'lemonadep' 'mata' 'oriole' 'sake' 'sargo' 'raven' 'TP1803')

function lunch_devices() {
    add_lunch_combo statix_${device}-user
    add_lunch_combo statix_${device}-userdebug
}

for device in ${devices[@]}; do
    lunch_devices
done

cat vendor/statix/bootanimation/bootanimation.00 vendor/statix/bootanimation/bootanimation.01 > vendor/statix/bootanimation/bootanimation.zip
