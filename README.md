# dtopo

Domino network topology.

## Overview

This tools would generate Mail Routing/Replicating server topology of IBM Domino from Domino Directory.

When you specify Domino Directory(names.nsf) in dtopo.ini and run dtopo.java, it will show topology charts of Replication/Mail Routing information from its connection documents.

## Background

I have found these files recently. File timestamps shows that I wrote them around 2001-2002. I have rewrote them in UTF-8, and I have changed a bit for Eclipse environment. Now I would make them public in github with my nostalgy.

## Setup

- Install and Setup IBM Notes.

- Prepare names.nsf, which contains enough information of Mail Routings/Replicatings, in your local Notes Data directory. For example, test/names.nsf

- Install Eclipse.

- git clone/download dtopo from [github](https://github.com/dotnsf/dtopo) into your Eclipse workspace as project.

- Change your JRE in Eclipse project as the ones of IBM Notes. See detailed information [here](http://dotnsf.blog.jp/archives/1067783810.html).

- Add Notes.jar and websvc.jar as external JARs in your project.

- Edit dtopo.ini and set your names.nsf path following with n: 

    - n: test/names.nsf

- Build dtopo.java, and run it. You will see two windows displayed. One is for Mail Routing, and another one is for Replicating.

    - You can drag and drop server icons in them, and reorganize topologies.

## Licensing

This code is licensed under MIT.

## Copyright

2017 K.Kimura @ Juge.Me all rights reserved.
