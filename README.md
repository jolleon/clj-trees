# trees

Backend app for [Yelp Clojure class assignment 2][1]
This provides REST access to a dataset of all the trees of San Francisco!

Data comes from: https://data.sfgov.org/Public-Works/Street-Tree-List/tkzw-k3nq

The app provides 2 endpoints:
/species gives a list of all the species with their count (sorted by decreasing frequency)
/trees/<species>&<other species>&...  gives a list of coordinates of all the trees of any of the given species

[1]: https://trac.yelpcorp.com/wiki/Clojure/Assignment2

