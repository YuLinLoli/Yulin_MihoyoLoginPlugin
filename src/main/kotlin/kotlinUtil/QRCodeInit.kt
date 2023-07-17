package com.yulin.kotlinUtil

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO

class QRCodeInit {

    companion object {


        private const val BLACK = 0xFF000000
        private const val WHITE = 0xFFFFFFFF

        /**
         * 生成二维码
         * @param content       扫码内容
         * @param qrcodeWidth   二维码宽度
         * @param qrCodeHeigh   二维码高度
         * @param logoWidth     logo宽度
         * @param logoHeigh     logo高度
         * @param logoPath      logo相对路径(null时不加logo）
         * @param qrCodeColor   二维码颜色
         * @return
         * @throws Exception
         */
        suspend fun qrCodeGenerate(
            content: String,
            qrcodeWidth: Int,
            qrCodeHeigh: Int,
            logoWidth: Int,
            logoHeigh: Int,
            logoPath: String,
            qrCodeColor: Int
        ): ExternalResource {
            /** 定义Map集合封装二维码配置信息 */
            val hints = HashMap<EncodeHintType, Any>()
            /** 设置二维码图片的内容编码 */
            hints[EncodeHintType.CHARACTER_SET] = "utf-8"
            /** 设置二维码图片的上、下、左、右间隙 */
            hints[EncodeHintType.MARGIN] = 1
            /** 设置二维码的纠错级别 */
            hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
            /**
             * 创建二维码字节转换对象
             * 第一个参数：二维码图片中的内容
             * 第二个参数：二维码格式器
             * 第三个参数：生成二维码图片的宽度
             * 第四个参数：生成二维码图片的高度
             * 第五个参数：生成二维码需要配置信息
             *  */
            val matrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, qrcodeWidth, qrCodeHeigh, hints)

            /** 获取二维码图片真正的宽度  */
            val matrixWidth = matrix.width

            /** 获取二维码图片真正的高度  */
            val matrixHeight = matrix.height

            /** 定义一张空白的缓冲流图片 */
            val image = BufferedImage(matrixWidth, matrixHeight, BufferedImage.TYPE_INT_RGB)
            /** 把二维码字节转换对象 转化 到缓冲流图片上 */
            for (x in 0 until matrixWidth) {

                for (y in 0 until matrixHeight) {
                    /** 通过x、y坐标获取一点的颜色 true: 黑色  false: 白色 */
                    val rgb = if (matrix.get(x, y)) BLACK else WHITE
                    image.setRGB(x, y, rgb.toInt())
                }
            }

            if (logoPath != "null") {
                /** 获取logo图片 */
                /**
                 * static 内不能用this获取绝对路径
                 * 通过getCanonicalPath获取当前程序的绝对路径
                 */
                val file = File("")//参数为空
                val courseFile = withContext(Dispatchers.IO) {
                    file.canonicalPath
                }
                val path = "$courseFile/C:/hwx/$logoPath"
//            System.out.println(path);
                val logo = withContext(Dispatchers.IO) {
                    ImageIO.read(File(path))
                }

                /** 获取缓冲流图片的画笔 */
                val g: Graphics2D = image.graphics as Graphics2D

                /** 在二维码图片中间绘制logo */
                g.drawImage(
                    logo, (matrixWidth - logoWidth) / 2,
                    (matrixHeight - logoHeigh) / 2,
                    logoWidth, logoHeigh, null
                )

                /** 设置画笔的颜色 */
                g.color = Color.WHITE
                /** 设置画笔的粗细 */
                g.stroke = BasicStroke(5.0f)
                /** 设置消除锯齿 */
                g.setRenderingHint(
                    RenderingHints
                        .KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON
                )
                /** 绘制圆角矩形 */
                g.drawRoundRect(
                    (matrixWidth - logoWidth) / 2,
                    (matrixHeight - logoHeigh) / 2,
                    logoWidth, logoHeigh, 10, 10
                )

            }

            val os = ByteArrayOutputStream()

            withContext(Dispatchers.IO) {
                ImageIO.write(image, "jpg", os)
            }
            // ImageIO.write(bi, "jpg", new File(imagePath));
            //   File file = new File(imagePath);
            //  inputStream = new FileInputStream(file);


            return ByteArrayInputStream(os.toByteArray()).toExternalResource()

        }
    }

}