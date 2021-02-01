function join_by { local d=$1; shift; local f=$1; shift; printf %s "$f" "${@/#/$d}"; }

filename="ui_test_paths"
packages=()
while read -r line; do
    name="$line"
    packages+="package $name.*"
done < "$filename"

result= join_by , $packages
echo $result


