import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvMinMaxLoc;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.GaussianBlur;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacv.VideoInputFrameGrabber;

public class GrabImage implements Runnable {
	IplImage image;
	CanvasFrame canvas1;
	Robot robot;
	static int SELECTED_DEVICE_NUMBER;
	static String DEVICE_DESCRIPTION;
	OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();

	public GrabImage() {

		canvas1 = new CanvasFrame("");
		SELECTED_DEVICE_NUMBER = 0;

		canvas1.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
		canvas1.setMaximumSize(new Dimension(500, 500));
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		IplImage img = new IplImage();
		Loader.load(opencv_objdetect.class);
		FrameGrabber grabber = new OpenCVFrameGrabber(SELECTED_DEVICE_NUMBER);
		grabber.setImageHeight(320);
		grabber.setImageWidth(480);
		CvPoint point = null;

		try {
			grabber.start();
			DEVICE_DESCRIPTION = VideoInputFrameGrabber.getDeviceDescriptions()[SELECTED_DEVICE_NUMBER];
			canvas1.setTitle(DEVICE_DESCRIPTION + " - Webcam capture");
			canvas1.setVisible(true);

			while (MainWindow.runThreads) {
				img = grabberConverter.convert(grabber.grab());
				if (img != null) {
					IplImage grayImage = cvCreateImage(cvGetSize(img), IPL_DEPTH_8U, 1);
					cvCvtColor(img, grayImage, CV_BGR2GRAY);
					canvas1.showImage(grabberConverter.convert(grayImage));
					Mat denoised = new Mat();
					Mat gray = new Mat(grayImage);
					GaussianBlur(gray, denoised, new Size(5, 5), 0.0d);
					DoublePointer min_val = new DoublePointer();
					DoublePointer max_val = new DoublePointer();

					CvPoint minLoc = new CvPoint();
					CvPoint maxLoc = new CvPoint();
					cvMinMaxLoc(grayImage, min_val, max_val, minLoc, maxLoc, null);
					point = new CvPoint();
					point.x(maxLoc.x() + grayImage.width());
					point.y(maxLoc.y() + grayImage.height());
					robot.mouseMove(point.x(), point.y());
				}
			}

			grabber.stop();
			System.out.println("Done. Exit.");
			System.exit(0);

		} catch (Exception e) {
			System.out.println("ERROR: GrabImage " + e.getMessage());
			e.printStackTrace();
		} finally {
			point.close();
			canvas1.dispose();
			img.close();
			System.gc();
		}
	}

	public BufferedImage IplImageToBufferedImage(IplImage src) {
		Java2DFrameConverter paintConverter = new Java2DFrameConverter();
		Frame frame = grabberConverter.convert(src);
		return paintConverter.getBufferedImage(frame, 1);
	}
}