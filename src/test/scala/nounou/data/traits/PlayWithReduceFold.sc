val vec = Vector(1, 2, 3, 4)
val vec1 = Vector(100)
val vec0 = Vector[Int]()

vec.foldLeft(10){ _ + _ }
vec.reduceLeft{ _ + _ }
vec.scanLeft(0){ _ + _ }
vec.scanLeft(0){ _ + _ }.init

vec1.foldLeft(10){ _ + _ }
vec1.reduceLeft{ _ + _ }
vec1.scanLeft(0){ _ + _ }
vec1.scanLeft(0){ _ + _ }.init

vec0.foldLeft(10){ _ + _ }
//vec0.reduceLeft{ _ + _ }
vec0.scanLeft(0){ _ + _ }
vec0.scanLeft(0){ _ + _ }.init