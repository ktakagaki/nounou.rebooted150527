val vec = Vector(1, 2, 3, 4)
vec.foldLeft(10){ _ + _ }
vec.reduceLeft{ _ + _ }
vec.scanLeft(0){ _ + _ }
vec.scanLeft(0){ _ + _ }.init