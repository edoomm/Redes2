package InformationObjects;

import java.io.File;
import java.io.Serializable;

public class FragmentDownloadInformation implements Serializable {
    private File fileName;
    private long initialPosition;
    private long fragmentSize;

    public FragmentDownloadInformation(File fileName, long initialPosition, long fragmentSize) {
        this.fileName = fileName;
        this.initialPosition = initialPosition;
        this.fragmentSize = fragmentSize;
    }

    public File getFile() {
        return fileName;
    }

    public void setFile(File fileName) {
        this.fileName = fileName;
    }

    public long getInitialPosition() {
        return initialPosition;
    }

    public void setInitialPosition(long initialPosition) {
        this.initialPosition = initialPosition;
    }

    public long getFragmentSize() {
        return fragmentSize;
    }

    public void setFragmentSize(long fragmentSize) {
        this.fragmentSize = fragmentSize;
    }
}
