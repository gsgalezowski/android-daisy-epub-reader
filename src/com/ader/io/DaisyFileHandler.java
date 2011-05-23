/**
 * Abstracts File operations for DAISY content.
 * 
 * The aim is to allow transparent access to content in compressed (zip) files,
 * as well as uncompressed, individual files. It's intended to cope with all
 * the various and varied files used for Daisy content e.g. structure, SMIL,
 * audio, etc.
 * 
 * This is an experimental interface which needs to be tested.
 * 
 * TODO(jharty): decide whether we'd like to support nested zip files e.g.
 * where an entire library of zip files could be enclosed in a master zip file.
 */
package com.ader.io;

/**
 * Allows DAISY files to be read and processed.
 * 
 * @author Julian Harty
 */
public interface DaisyFileHandler {

}
