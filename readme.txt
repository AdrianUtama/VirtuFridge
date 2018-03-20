Creating your branch:
$ git branch -b <name of your branch>

Checkout your branch:
$ git checkout <name of your branch>

Work on your branch and make commits and push:
$ git push origin <name of your branch>
$ git push -f origin <name of your branch>
  -- use this method if your push is rejected, but you know your local is correct

Always make sure that your branch is up-to-date with master before merging
When you are ready to merge:
$ git checkout master
$ git pull
$ git checkout <name of branch>
$ git rebase -i origin/mater
Squash your commits into one and resolve merge conflicts if there are any
Rebasing creates another commit, so make sure to push after rebasing

Merge your branch with master. Be EXTRA careful with the syntax
$ git push origin <name of branch>:master