package com.ader.smil;

import junit.framework.TestCase;

public class SmilParserTest extends TestCase {
    private static final String AUDIO2_MP3 = "audio2.mp3";
    private static final String AUDIO1_MP3 = "audio1.mp3";
    private SmilParser parser = new SmilParser();
    private static final String AUDIO_1 = "<audio src=\"audio1.mp3\" clip-begin=\"npt=0.000s\" clip-end=\"npt=5.381s\" id=\"audio_0001\"/>";
    private static final String AUDIO_2 = "<audio src=\"audio2.mp3\" clip-begin=\"npt=2.002s\" clip-end=\"npt=5.381s\" id=\"audio_0002\"/>";
    private static final String AUDIO_SEQ = String.format("<seq>%s</seq>", AUDIO_1 + AUDIO_2);
    private static final String SEQ_TEMPLATE = "<body><seq>%s</seq></body>";
    private static final String TEXT_1 =  "<text src=\"ncc.html#icth_0001\" id=\"icth_0001\"/>";
    private static final String TEXT_2 =  "<text src=\"ncc.html#icth_0002\" id=\"icth_0002\"/>";
    private static final String PARA_TEMPLATE = "<body><seq dur=\"1276.310s\"><par endsync=\"last\">%s</par></seq></body>";
    
    
    public void testTextElement() throws Exception {
        SequenceElement seq = parser.parse(String.format(PARA_TEMPLATE, TEXT_1));
        assertEquals("ncc.html#icth_0001", seq.getTextElement().getSrc());
        assertEquals("How many elements should the example consist of?", 1, seq.size());
        assertEquals("Null should be returned when there is no audio element.", null, seq.get(0).getAudioElement());
        assertTrue("The first element should be a Parallel Element.", seq.get(0) instanceof ParallelElement);
    }
    
    public void testAudioElement() throws Exception {
        SequenceElement seq = parser.parse(String.format(SEQ_TEMPLATE, AUDIO_1));
        assertEquals("Null should be returned for the text source", null, seq.getTextElement());
        assertEquals("Audio filename needs to be correct", AUDIO1_MP3, seq.getAudioElement().getSrc());
        assertTrue("The first element should be a Audio Element.", seq.get(0) instanceof AudioElement);
    }
    
    public void testCombinedElement() throws Exception {
        SequenceElement seq = parser.parse(String.format(PARA_TEMPLATE, AUDIO_1 + TEXT_1));
        assertEquals("There should only be only one element for a parallel element", 1, seq.size());
        assertEquals("There should be an audio file source", AUDIO1_MP3, seq.getAudioElement().getSrc());
        assertEquals("ncc.html#icth_0001", seq.getTextElement().getSrc());
    }
    
    public void testOrderOfElementsIsIdempotic() throws Exception {
        SequenceElement seqAT = parser.parse(String.format(PARA_TEMPLATE, AUDIO_1 + TEXT_1));
        SequenceElement seqTA = parser.parse(String.format(PARA_TEMPLATE, TEXT_1 + AUDIO_1));
        assertEquals("Contents of the elements should be identical regardless of the order of elements in the parallel sequence", seqAT, seqTA);
        assertEquals("Contents of the audio source should be identical regardless of order of the sub elements", seqAT.get(0).getAudioElement().getSrc(), seqTA.get(0).getAudioElement().getSrc());
    }
    
    public void testContentsOfNewArrayDontOverwriteExistingArray() throws Exception {
        SequenceElement seqAT = parser.parse(String.format(PARA_TEMPLATE, AUDIO_1 + TEXT_1));
        SequenceElement seqTA = parser.parse(String.format(PARA_TEMPLATE, TEXT_2 + AUDIO_2));
        assertFalse("Contents of the elements should be different for distinct elements contents", seqAT.equals(seqTA));
        assertEquals("There should be an audio file source", AUDIO1_MP3, seqAT.get(0).getAudioElement().getSrc());
        assertEquals("ncc.html#icth_0001", seqAT.get(0).getTextElement().getSrc());
        assertEquals("There should be an audio file source", AUDIO2_MP3, seqTA.get(0).getAudioElement().getSrc());
        assertEquals("ncc.html#icth_0002", seqTA.get(0).getTextElement().getSrc());
    }
    
    public void testSequenceCursor() throws Exception {
        SequenceElement seq = parser.parse(String.format(SEQ_TEMPLATE, AUDIO_1 + AUDIO_2));
        assertEquals(AUDIO1_MP3, seq.getAudioElement().getSrc());
        seq.next();
        assertEquals(AUDIO2_MP3, seq.getAudioElement().getSrc());
        seq.previous();
        assertEquals(AUDIO1_MP3, seq.getAudioElement().getSrc());
    }
    
    public void testParallelCursor() throws Exception {
        SequenceElement seq = parser.parse(String.format(PARA_TEMPLATE, AUDIO_SEQ));
        MediaElement par = seq.current();
        assertEquals(ParallelElement.class, par.getClass());
        MediaElement audio1 = par.current();
        assertEquals(AudioElement.class, audio1.getClass());
        assertEquals(AUDIO1_MP3, audio1.getAudioElement().getSrc());
        MediaElement audio2 = par.next();
        assertEquals(AudioElement.class, audio2.getClass());
        assertEquals(AUDIO2_MP3, audio2.getAudioElement().getSrc());
    }
    
    public void testGetAllAudioAttributes() throws Exception {
        SequenceElement seq = parser.parse(String.format(PARA_TEMPLATE, AUDIO_1));
        assertEquals("npt=0.000s", seq.getAudioElement().getClipBegin());
        assertEquals("npt=5.381s", seq.getAudioElement().getClipEnd());
        assertEquals("audio_0001", seq.getAudioElement().getId());
        // <audio src=\"audio2.mp3\" clip-begin=\"npt=2.002s\" clip-end=\"npt=5.381s\" id=\"audio_0002\"/>
    }
}
