aosp_devices=('angler' 'angler_4core' 'bonito' 'taimen' 'sargo')
caf_devices=('beryllium' 'oneplus3' 'mata')

function lunch_devices() {
    add_lunch_combo statix_${device}-user
    add_lunch_combo statix_${device}-userdebug
}

if [[ $( grep -i "external/json-c" .repo/manifests/include.xml) ]]; then
    for device in ${caf_devices[@]}; do
        lunch_devices
    done
else
    for device in ${aosp_devices[@]}; do
        lunch_devices
    done
fi
