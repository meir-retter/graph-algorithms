test file:

0 or 1 (undirected or directed respectively)
number of nodes
|E| edges, each on its own line. Example:
1 2
2 3
3 4
1 4

OR, with weights:

1 2 12
2 3 8
3 4 9
1 4 3

If the program is given a directed graph, it will output details of:

BFS
DFS
Dijkstra
edge classification
low
max flow

If the program is given an undirected graph, it will output details of:

BFS
DFS
articulation points
bridges
biconnected components

For searches, node 1 is always the starting node, so assign 1 to the node you'd like to start from.
For max flow, the source is node 1, and the sink is the last node
(e.g. node 6 in the six-node graph on p.717 in CLRS)

Obviously, test graphs must conform to the algorithms you want to use on them. For example, a directed graph
must have no negative weights for Dijkstra's algorithm to work on it, and the max flow algorithm won't work
on graphs with invalid flow or antiparallel edges.

I suggest you use testfile4.txt as as undirected graph for testing (because it's large, interesting, and
is on page 622 in CLRS) and testfile5.txt as a directed graph for testing (because it is valid for all of
the algorithms, including max flow, and it's on page 726 in CLRS)

If you'd like, you can use the name of your own test file as a command line argument, and my program will
test the appropriate algorithms on it (depending on whether it's directed). If no command line arguments
are included, the program will use the aforementioned test files, and test algorithms on both directed
and undirected graphs.


Here are links to images of the graphs given by testfile4.txt and testfile5.txt with the numbers as I have labeled
them, for reference:

testfile4.txt:
imgur.com/xuKdMns

testfile5.txt:
imgur.com/1UeOaz0


