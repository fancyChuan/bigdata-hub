package cn.fancychuan.spark3.simulation

class SubTask extends Serializable {
    var datas : List[Int] = _
    var logic : (Int)=>Int = _

    // 计算
    def compute() = {
        datas.map(logic)
    }
}
