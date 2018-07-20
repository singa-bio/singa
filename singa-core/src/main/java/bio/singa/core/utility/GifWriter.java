/*
  modified after http://elliot.kroo.net/software/java/GifSequenceWriter/
  original statement:
  GifSequenceWriter.java
  Created by Elliot Kroo on 2009-04-25.
  This work is licensed under the Creative Commons Attribution 3.0 Unported
  License. To view a copy of this license, visit
  http://creativecommons.org/licenses/by/3.0/ or send a letter to Creative
  Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 */
package bio.singa.core.utility;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

public class GifWriter {

    private static final int DEFAULT_IMAGE_TYPE = BufferedImage.TYPE_INT_ARGB_PRE;
    private static final int DEFAULT_TIME_BETWEEN_FRAMES = 1;
    private static final boolean DEFAULT_LOOP_CONTINUOUSLY = true;

    private ImageWriter gifWriter;
    private ImageWriteParam imageWriteParam;
    private IIOMetadata imageMetaData;
    private ImageOutputStream outputStream;

    /**
     * Creates a new GifSequenceWriter
     *
     * @param targetFilePath The location and name of the final gif file.
     * @param imageType one of the imageTypes specified in BufferedImage
     * @param timeBetweenFrames the time between frames in milliseconds
     * @param loopContinuously whether the gif should loop repeatedly
     * @author Elliot Kroo (elliot[at]kroo[dot]net)
     */
    public GifWriter(Path targetFilePath, int imageType, int timeBetweenFrames, boolean loopContinuously) throws IOException {
        gifWriter = getWriter();
        imageWriteParam = gifWriter.getDefaultWriteParam();
        ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(imageType);

        imageMetaData = gifWriter.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);
        initializeMetaData(timeBetweenFrames, loopContinuously);

        outputStream = new FileImageOutputStream(targetFilePath.toFile());
        gifWriter.setOutput(outputStream);

        gifWriter.prepareWriteSequence(null);
    }

    public GifWriter(Path targetFilePath) throws IOException {
        this(targetFilePath, DEFAULT_IMAGE_TYPE, DEFAULT_TIME_BETWEEN_FRAMES, DEFAULT_LOOP_CONTINUOUSLY);
    }

    private void initializeMetaData(int timeBetweenFrames, boolean loopContinuously) throws IIOInvalidTreeException {
        String metaFormatName = imageMetaData.getNativeMetadataFormatName();
        IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);
        IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");
        graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
        graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString(timeBetweenFrames / 10));
        graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");
        IIOMetadataNode commentsNode = getNode(root, "CommentExtensions");
        commentsNode.setAttribute("CommentExtension", "Created by MAH");
        IIOMetadataNode applicationExtensionsPatent = getNode(root, "ApplicationExtensions");
        IIOMetadataNode applicationExtensionsChild = new IIOMetadataNode("ApplicationExtension");
        applicationExtensionsChild.setAttribute("applicationID", "NETSCAPE");
        applicationExtensionsChild.setAttribute("authenticationCode", "2.0");
        int loop = loopContinuously ? 0 : 1;
        applicationExtensionsChild.setUserObject(new byte[]{0x1, (byte) (loop & 0xFF), (byte) ((loop >> 8) & 0xFF)});
        applicationExtensionsPatent.appendChild(applicationExtensionsChild);
        imageMetaData.setFromTree(metaFormatName, root);
    }

    /**
     * Appends a image to the end of the sequence.
     *
     * @param renderedImage The image to append.
     * @throws IOException If the file cannot be written of found.
     */
    public void writeToSequence(RenderedImage renderedImage) throws IOException {
        IIOImage image = new IIOImage(renderedImage, null, imageMetaData);
        gifWriter.writeToSequence(image, imageWriteParam);
    }

    /**
     * Close this GifSequenceWriter. This does not close the underlying stream, just finishes off the GIF.
     */
    public void close() throws IOException {
        gifWriter.endWriteSequence();
        outputStream.close();
    }

    /**
     * Returns the first available GIF ImageWriter using {@link ImageIO#getImageWritersBySuffix(String)}.
     *
     * @return A GIF ImageWriter.
     * @throws IIOException If no GIF image writers are available.
     */
    private static ImageWriter getWriter() throws IIOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix("gif");
        if (!writers.hasNext()) {
            throw new IIOException("No GIF Image Writers Exist");
        } else {
            return writers.next();
        }
    }

    /**
     * Returns an existing child node, or creates and returns a new child node (if the requested node does not exist).
     *
     * @param rootNode The IIOMetadataNode to search for the child node.
     * @param nodeName The name of the child node.
     * @return The child node, if found or a new node created with the given name.
     */
    private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
        int numberOfNodes = rootNode.getLength();
        for (int i = 0; i < numberOfNodes; i++) {
            if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName) == 0) {
                return ((IIOMetadataNode) rootNode.item(i));
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        rootNode.appendChild(node);
        return node;
    }


}