package iconify.utils

external interface Color {
    var type: String
}

external interface RGBColor : Color {
    var r: Double
    var g: Double
    var b: Double
    var alpha: Double
}

external interface HSLColor : Color {
    var h: Double
    var s: Double
    var l: Double
    var alpha: Double
}

external interface LABColor : Color {
    var l: Double
    var a: Double
    var b: Double
    var alpha: Double
}

external interface LCHColor : Color {
    var l: Double
    var c: Double
    var h: Double
    var alpha: Double
}

external interface FunctionColor : Color {
    var `func`: String
    var value: String
}

external interface TransparentColor : Color

external interface NoColor : Color

external interface CurrentColor : Color
