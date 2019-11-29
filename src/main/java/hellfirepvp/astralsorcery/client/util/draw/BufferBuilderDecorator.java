/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.draw;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

import java.awt.*;
import java.nio.ByteBuffer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BufferBuilderDecorator
 * Created by HellFirePvP
 * Date: 28.11.2019 / 20:08
 */
public class BufferBuilderDecorator extends BufferBuilder {

    private final BufferBuilder buffer;

    private NormalDecorator normalDecorator;
    private LightmapDecorator lightmapDecorator;
    private UVDecorator uvDecorator;
    private ColorDecorator colorDecorator;

    private BufferBuilderDecorator(BufferBuilder decorated) {
        super(0);
        this.buffer = decorated;
    }

    public static BufferBuilderDecorator decorate(BufferBuilder buf) {
        return new BufferBuilderDecorator(buf);
    }

    ///////////////////////////////////////////////////////////////////////////
    //      Decoration options
    ///////////////////////////////////////////////////////////////////////////

    public void setNormalDecorator(NormalDecorator normalDecorator) {
        this.normalDecorator = normalDecorator;
    }

    public void setLightmapDecorator(LightmapDecorator lightmapDecorator) {
        this.lightmapDecorator = lightmapDecorator;
    }

    public void setUvDecorator(UVDecorator uvDecorator) {
        this.uvDecorator = uvDecorator;
    }

    public void setColorDecorator(ColorDecorator colorDecorator) {
        this.colorDecorator = colorDecorator;
    }

    public void withTranslation(double x, double y, double z, Runnable run) {
        this.buffer.setTranslation(x, y, z);
        try {
            run.run();
        } finally {
            this.buffer.setTranslation(0, 0, 0);
        }
    }

    public boolean hasDecorators() {
        return this.normalDecorator != null ||
                this.lightmapDecorator != null ||
                this.uvDecorator != null ||
                this.colorDecorator != null;
    }

    ///////////////////////////////////////////////////////////////////////////
    //      Methods with decorated changes
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public BufferBuilder color(int red, int green, int blue, int alpha) {
        if (this.colorDecorator != null) {
            int[] newColor = this.colorDecorator.decorate(red, green, blue, alpha);
            return this.buffer.color(newColor[0], newColor[1], newColor[2], newColor[3]);
        }
        return this.buffer.color(red, green, blue, alpha);
    }

    @Override
    public void putColorRGBA(int index, int red, int green, int blue) {
        if (this.colorDecorator != null) {
            int[] newColor = this.colorDecorator.decorate(red, green, blue, 255);
            this.buffer.putColorRGBA(newColor[0], newColor[1], newColor[2], newColor[3]);
            return;
        }
        this.buffer.putColorRGBA(index, red, green, blue);
    }

    @Override
    public void putColorRGBA(int index, int red, int green, int blue, int alpha) {
        if (this.colorDecorator != null) {
            int[] newColor = this.colorDecorator.decorate(red, green, blue, alpha);
            this.buffer.putColorRGBA(newColor[0], newColor[1], newColor[2], newColor[3]);
            return;
        }
        this.buffer.putColorRGBA(index, red, green, blue, alpha);
    }

    @Override
    public BufferBuilder tex(double u, double v) {
        if (this.uvDecorator != null) {
            double[] newUV = this.uvDecorator.decorate(u, v);
            return this.buffer.tex(newUV[0], newUV[1]);
        }
        return this.buffer.tex(u, v);
    }

    @Override
    public BufferBuilder lightmap(int skyLight, int blockLight) {
        if (this.lightmapDecorator != null) {
            float[] newLight = this.lightmapDecorator.decorate(skyLight, blockLight);
            return this.buffer.lightmap(Math.round(newLight[0]), Math.round(newLight[1]));
        }
        return this.buffer.lightmap(skyLight, blockLight);
    }

    @Override
    public BufferBuilder normal(float x, float y, float z) {
        if (this.normalDecorator != null) {
            float[] newNormals = this.normalDecorator.decorate(x, y, z);
            return this.buffer.normal(newNormals[0], newNormals[1], newNormals[2]);
        }
        return this.buffer.normal(x, y, z);
    }

    @Override
    public void putNormal(float x, float y, float z) {
        if (this.normalDecorator != null) {
            float[] newNormals = this.normalDecorator.decorate(x, y, z);
            this.buffer.putNormal(newNormals[0], newNormals[1], newNormals[2]);
            return;
        }
        this.buffer.putNormal(x, y, z);
    }

    @Override
    public void addVertexData(int[] vertexData) {
        if (!this.hasDecorators()) {
            this.buffer.addVertexData(vertexData);
            return;
        }

        VertexFormat format = this.getVertexFormat();
        int elementCount = format.getElementCount();
        int vertexLengthInt = format.getIntegerSize();

        int overlayColor = this.buffer.isColorDisabled() ? -1 : format.getColorOffset();
        int overlayNormals = format.getNormalOffset();
        int overlayUV = format.getElements().indexOf(DefaultVertexFormats.TEX_2F);
        int overlayLightmap = format.getElements().indexOf(DefaultVertexFormats.TEX_2S);

        for (int vertexIndex = 0; vertexIndex < vertexData.length / vertexLengthInt; vertexIndex++) {
            for (int elementIndex = 0; elementIndex < elementCount; elementIndex++) {
                VertexFormatElement element = format.getElement(elementIndex);
                switch (element.getUsage()) {
                    case NORMAL:
                        if (overlayNormals == -1) {
                            continue;
                        }
                        break;
                    case COLOR:
                        if (overlayColor == -1) {
                            continue;
                        }
                        break;
                    case UV:
                        if (element.equals(DefaultVertexFormats.TEX_2F) && overlayUV != -1) {
                            break;
                        } else if (element.equals(DefaultVertexFormats.TEX_2S) && overlayLightmap != -1) {
                            break;
                        } else {
                            continue;
                        }
                    default:
                        continue;
                }

                VertexFormatElement.Type elementType = element.getType();
                int typeSize = elementType.getSize();
                int elementOffset = format.getOffset(elementIndex);
                int vertexStart = vertexIndex * format.getSize() + elementOffset;
                int dataMask = (0x100 << (0b1000 * (typeSize - 1))) - 1;

                //Unpack vertex data to be decorated
                float[] vData = new float[Math.min(element.getElementCount(), 4)];
                for (int i = 0; i < vData.length; i++) {
                    int vPos = vertexStart + typeSize * i;
                    int index = vPos >> 2;
                    int offset = vPos & 3;
                    int bits = vertexData[index];
                    bits = bits >>> (offset * 8);
                    if((vPos + typeSize - 1) / 4 != index) {
                        bits |= vertexData[index + 1] << ((4 - offset) * 8);
                    }
                    bits &= dataMask;
                    vData[i] = unpack(bits, dataMask, elementType);
                }

                //Yes we can safely just use the decorators as we checked this before.
                if (element.getUsage() == VertexFormatElement.Usage.NORMAL) {
                    float[] newNormals = this.normalDecorator.decorate(vData[0], vData[1], vData[2]);
                    System.arraycopy(newNormals, 0, vData, 0, 3);
                }
                if (element.getUsage() == VertexFormatElement.Usage.COLOR) {
                    float[] newColor = this.colorDecorator.decorate(vData[0], vData[1], vData[2], vData[3]);
                    System.arraycopy(newColor, 0, vData, 0, 4);
                }
                if (element.getUsage() == VertexFormatElement.Usage.UV) {
                    if (element.equals(DefaultVertexFormats.TEX_2F) && this.uvDecorator != null) {
                        double[] newUV = this.uvDecorator.decorate(vData[0], vData[1]);
                        vData[0] = (float) newUV[0];
                        vData[1] = (float) newUV[1];
                    } else if (element.equals(DefaultVertexFormats.TEX_2S) && this.lightmapDecorator != null) {
                        float skyLight   = (vData[0] * 0xFFFF) / 0x20;
                        float blockLight = (vData[1] * 0xFFFF) / 0x20;
                        float[] newLmap = this.lightmapDecorator.decorate(skyLight, blockLight);
                        vData[0] = newLmap[0] * 0x20 / 0xFFFF;
                        vData[1] = newLmap[1] * 0x20 / 0xFFFF;
                    }
                }

                //Repack decorated vertex data
                for (int i = 0; i < vData.length; i++) {
                    int vPos = vertexStart + typeSize * i;
                    int index = vPos >> 2;
                    int offset = vPos & 3;
                    float value = vData[i];

                    int bData = pack(value, dataMask, elementType);
                    vertexData[index] &= ~(dataMask << (offset * 8));
                    vertexData[index] |= (((bData & dataMask) << (offset * 8)));
                }
            }
        }
        this.buffer.addVertexData(vertexData);
    }

    private float unpack(int data, int dataMask, VertexFormatElement.Type vType) {
        if (vType == VertexFormatElement.Type.FLOAT) {
            return Float.intBitsToFloat(data);
        } else if (vType == VertexFormatElement.Type.UBYTE || vType == VertexFormatElement.Type.USHORT) {
            return (float) data / dataMask;
        } else if (vType == VertexFormatElement.Type.UINT) {
            return (float) ((double) (data & 0xFFFFFFFFL) / 0xFFFFFFFFL);
        } else if (vType == VertexFormatElement.Type.BYTE) {
            return ((float) (byte) data) / (dataMask >> 1);
        } else if (vType == VertexFormatElement.Type.SHORT) {
            return ((float) (short) data) / (dataMask >> 1);
        } else if (vType == VertexFormatElement.Type.INT) {
            return (float)((double) (data & 0xFFFFFFFFL) / (0xFFFFFFFF >> 1));
        }
        return 0;
    }

    private int pack(float data, int dataMask, VertexFormatElement.Type vType) {
        if (vType == VertexFormatElement.Type.FLOAT) {
            return Float.floatToRawIntBits(data);
        } else if(vType == VertexFormatElement.Type.UBYTE ||
                        vType == VertexFormatElement.Type.USHORT ||
                        vType == VertexFormatElement.Type.UINT) {
            return Math.round(data * dataMask);
        } else {
            return Math.round(data * (dataMask >> 1));
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //      Other, unchanged, redirected methods
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void setTranslation(double x, double y, double z) {
        this.buffer.setTranslation(x, y, z);
    }

    @Override
    public void reset() {
        this.buffer.reset();
    }

    @Override
    public void begin(int glMode, VertexFormat format) {
        this.buffer.begin(glMode, format);
    }

    @Override
    public State getVertexState() {
        return this.buffer.getVertexState();
    }

    @Override
    public void setVertexState(State state) {
        this.buffer.setVertexState(state);
    }

    @Override
    public void sortVertexData(float cameraX, float cameraY, float cameraZ) {
        this.buffer.sortVertexData(cameraX, cameraY, cameraZ);
    }

    @Override
    public int getColorIndex(int vertexIndex) {
        return this.buffer.getColorIndex(vertexIndex);
    }
    //No need to decorate this

    @Override
    public void putColorMultiplier(float red, float green, float blue, int vertexIndex) {
        this.buffer.putColorMultiplier(red, green, blue, vertexIndex);
    }

    @Override
    public void noColor() {
        this.buffer.noColor();
    }

    @Override
    public boolean isColorDisabled() {
        return this.buffer.isColorDisabled();
    }

    @Override
    public void putBulkData(ByteBuffer buffer) {
        this.buffer.putBulkData(buffer);
    }

    @Override
    public void endVertex() {
        this.buffer.endVertex();
    }

    @Override
    public void finishDrawing() {
        this.buffer.finishDrawing();
    }

    @Override
    public ByteBuffer getByteBuffer() {
        return this.buffer.getByteBuffer();
    }

    @Override
    public int getDrawMode() {
        return this.buffer.getDrawMode();
    }

    @Override
    public int getVertexCount() {
        return this.buffer.getVertexCount();
    }

    @Override
    public VertexFormat getVertexFormat() {
        return this.buffer.getVertexFormat();
    }

    //Decorated in .color(int, int, int, int)
    @Override
    public BufferBuilder color(float red, float green, float blue, float alpha) {
        return super.color(red, green, blue, alpha);
    }

    //Decorating this in putRGBA
    @Override
    public void putColor4(int argb) {
        super.putColor4(argb);
    }

    //Again, this is decorated in putRGBA
    @Override
    public void putColorRGB_F4(float red, float green, float blue) {
        super.putColorRGB_F4(red, green, blue);
    }

    //Decorated in putRGBA
    @Override
    public void putColorRGB_F(float red, float green, float blue, int vertexIndex) {
        super.putColorRGB_F(red, green, blue, vertexIndex);
    }

    @Override
    public void putBrightness4(int vertex0, int vertex1, int vertex2, int vertex3) {
        this.buffer.putBrightness4(vertex0, vertex1, vertex2, vertex3);
    }

    @Override
    public void putPosition(double x, double y, double z) {
        this.buffer.putPosition(x, y, z);
    }

    //One can already do .setTranslation to add an offset. no need to decorate this
    @Override
    public BufferBuilder pos(double x, double y, double z) {
        return this.buffer.pos(x, y, z);
    }

    ///////////////////////////////////////////////////////////////////////////
    //      Decorator interfaces
    ///////////////////////////////////////////////////////////////////////////

    public static interface NormalDecorator {

        public float[] decorate(float x, float y, float z);

    }

    public static interface LightmapDecorator {

        //0-15 each, return same
        public float[] decorate(float skyLight, float blockLight);

    }

    public static interface UVDecorator {

        public double[] decorate(double u, double v);

    }

    public static interface ColorDecorator {

        default float[] decorate(float r, float g, float b, float a) {
            int[] decorated = this.decorate((int) (r * 255.0F), (int) (g * 255.0F), (int) (b * 255.0F), (int) (a * 255.0F));
            return new float[] { decorated[0] / 255F, decorated[1] / 255F, decorated[2] / 255F, decorated[3] / 255F };
        }

        public int[] decorate(int r, int g, int b, int a);

    }

}
