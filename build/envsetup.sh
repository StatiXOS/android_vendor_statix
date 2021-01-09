function __print_extra_functions_help() {
cat <<EOF
Additional functions:
- repopick:        Utility to fetch changes from Gerrit.
EOF
}

function breakfast()
{
    target=$1
    STATIX_DEVICES_ONLY="true"
    unset LUNCH_MENU_CHOICES
    add_lunch_combo full-eng
    for f in `/bin/ls vendor/statix/vendorsetup.sh 2> /dev/null`
        do
            echo "including $f"
            . $f
        done
    unset f

    if [ $# -eq 0 ]; then
        # No arguments, so let's have the full menu
        echo "Nothing to eat for breakfast?"
        lunch
    else
        echo "z$target" | grep -q "-"
        if [ $? -eq 0 ]; then
            # A buildtype was specified, assume a full device name
            lunch $target
        else
            # This is probably just the StatiX model name
            lunch statix_$target-userdebug
        fi
    fi
    return $?
}

alias bib=breakfast

function brunch()
{
    breakfast $*
    if [ $? -eq 0 ]; then
        time m bacon
    else
        echo "No such item in brunch menu. Try 'breakfast'"
        return 1
    fi
    return $?
}

function repopick() {
    set_stuff_for_environment
    T=$(gettop)
    $T/vendor/statix/build/tools/repopick.py $@
}

function aospmerge()
{
    target_branch=$1
    set_stuff_for_environment
    T=$(gettop)
    python3 $T/vendor/statix/scripts/merge-aosp.py target_branch
}
