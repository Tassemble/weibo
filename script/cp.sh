mvn clean package
rm /home/fs/chq/work/tmpgit/players/*/lib/weibo-1*.jar
weibo=`ls -t1 target/weibo*jar | head -n1`
pathParent=/home/fs/chq/work/tmpgit/players/
paths=`ls $pathParent -t1`; 
for path in $paths;
do
 cp $weibo $pathParent$path/lib;
 echo "copy to $weibo $pathParent$path";
done;
/home/fs/chq/work/tmpgit/dg.sh -m "change weibo"
