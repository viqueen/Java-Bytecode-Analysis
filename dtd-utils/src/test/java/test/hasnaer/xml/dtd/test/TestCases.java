package hasnaer.xml.dtd.test;

import hasnaer.xml.dtd.DTD;
import hasnaer.xml.dtd.DTD2Class;
import java.io.FileInputStream;
import java.util.Arrays;
import junit.framework.TestSuite;
import org.junit.Test;

/**
 *
 * @author hasnae rehioui
 */
public class TestCases extends TestSuite {

    @Test
    public void domxml() throws Exception {

        String _rootPackage = "hasnaer.musicxml";
        String _outputDir = "output_mxml_v4";
        System.err.println("@@@@@@@@@ COMMON");
        DTD _commonDtd = new DTD(_rootPackage + ".common",
                new FileInputStream("common.mod"), null);
        System.err.println(_commonDtd);
//        _commonDtd.toJavaBeans(_outputDir);

        System.err.println("@@@@@@@@@ LAYOUT");
        DTD _layoutDtd = new DTD(_rootPackage + ".layout",
                new FileInputStream("layout.mod"),
                Arrays.asList(new DTD[]{_commonDtd}));
        System.err.println(_layoutDtd);
//        _layoutDtd.toJavaBeans(_outputDir);

        System.err.println("@@@@@@@@@ IDENTITY");
        DTD _identityDtd = new DTD(_rootPackage + ".identity",
                new FileInputStream("identity.mod"),
                Arrays.asList(new DTD[]{_layoutDtd}));
        System.err.println(_layoutDtd);
//        _identityDtd.toJavaBeans(_outputDir);

        System.err.println("@@@@@@@@@ ATTRIBUTES");
        DTD _attributesDtd = new DTD(_rootPackage + ".attributes",
                new FileInputStream("attributes.mod"),
                Arrays.asList(new DTD[]{_identityDtd}));
        System.err.println(_attributesDtd);
//        _attributesDtd.toJavaBeans(_outputDir);

        System.err.println("@@@@@@@@@ LINK");
        DTD _linkDtd = new DTD(_rootPackage + ".link",
                new FileInputStream("link.mod"),
                Arrays.asList(new DTD[]{_attributesDtd}));
        System.err.println(_linkDtd);
//        _linkDtd.toJavaBeans(_outputDir);

        System.err.println("@@@@@@@@@ NOTE");
        DTD _noteDtd = new DTD(_rootPackage + ".note",
                new FileInputStream("note.mod"),
                Arrays.asList(new DTD[]{_linkDtd}));
        System.err.println(_noteDtd);
//        _noteDtd.toJavaBeans(_outputDir);

        System.err.println("@@@@@@@@@ BARLINE");
        DTD _barlineDtd = new DTD(_rootPackage + ".barline",
                new FileInputStream("barline.mod"),
                Arrays.asList(new DTD[]{_noteDtd}));
        System.err.println(_barlineDtd);
//        _barlineDtd.toJavaBeans(_outputDir);

        System.err.println("@@@@@@@@@ DIRECTION");
        DTD _directionDtd = new DTD(_rootPackage + ".direction",
                new FileInputStream("direction.mod"),
                Arrays.asList(new DTD[]{_barlineDtd}));
        System.err.println(_directionDtd);
//        _directionDtd.toJavaBeans(_outputDir);

        System.err.println("@@@@@@@@@ SCORE ");
        DTD _scoreDtd = new DTD(_rootPackage + ".score",
                new FileInputStream("score.dtd"),
                Arrays.asList(new DTD[]{_directionDtd}));
        System.err.println(_directionDtd);
//        _scoreDtd.toJavaBeans(_outputDir);

        System.err.println("@@@@@@@@@ SCORE TIMEWISE");
        DTD _timewiseDtd = new DTD(_rootPackage + ".score.timewise",
                new FileInputStream("timewise-score.dtd"),
                Arrays.asList(new DTD[]{_scoreDtd}));
        System.err.println(_directionDtd);
//        _timewiseDtd.toJavaBeans(_outputDir);
        DTD2Class.execute(_timewiseDtd, _outputDir);

        System.err.println("@@@@@@@@@ SCORE PARTWISE");
        DTD _partwiseDtd = new DTD(_rootPackage + ".score.partwise",
                new FileInputStream("partwise-score.dtd"),
                Arrays.asList(new DTD[]{_scoreDtd}));
        System.err.println(_partwiseDtd);
//        _partwiseDtd.toJavaBeans(_outputDir);
        DTD2Class.execute(_partwiseDtd, _outputDir);
    }

    @Test
    public void dovxml() throws Exception {
        String _rootPackage = "hasnaer.voicexml";
        String _outputDir = "output_vxml_v4";
        System.err.println("@@@@@@@@@ VXML");

        DTD2Class.execute("vxml.dtd", "", _rootPackage, _outputDir, null);

    }

    @Test
    public void doxmlschema() throws Exception {
        String _rootPackage = "hasnaer.xsd";
        String _outputDir = "output_xsd_v4";
        System.err.println("@@@@@@@@@ XSD");
        DTD2Class.execute("xmlschema.dtd", "", _rootPackage, _outputDir, null);
    }


}