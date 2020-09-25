
how to use this command
```
$ ssh gerrit.transsion.com transsion
fatal: Available commands of transsion are:

   admin            administrator the trnassion cookbook
   compare-branch   compare two branch revision tag by git log comamnd

See 'transsion COMMAND --help' for more information.


```

```bash
$ ssh gerrit.transsion.com transsion compare-branch -h
transsion compare-branch [--] [--alias] [--help (-h)] --new revision --old revision --project (-p) PROJECT

 --                     : end of options
 --alias                : use native git comamnd or git alias command
 --help (-h)            : display this help text
 --new revision         : the new revision
 --old revision         : the old revision
 --project (-p) PROJECT : project for compare

comapre ci_tools git project between master branch and tssi branch
$ ssh gerrit.transsion.com transsion compare-branch -p ci_tools --new master --old tssi 

```
