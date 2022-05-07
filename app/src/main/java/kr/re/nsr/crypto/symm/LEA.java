package kr.re.nsr.crypto.symm;

import kr.re.nsr.crypto.BlockCipher;
import kr.re.nsr.crypto.engine.LeaEngine;
import kr.re.nsr.crypto.mac.CMac;
import kr.re.nsr.crypto.mode.CBCMode;
import kr.re.nsr.crypto.mode.CCMMode;
import kr.re.nsr.crypto.mode.CFBMode;
import kr.re.nsr.crypto.mode.CTRMode;
import kr.re.nsr.crypto.mode.ECBMode;
import kr.re.nsr.crypto.mode.GCMMode;
import kr.re.nsr.crypto.mode.OFBMode;

public class LEA {
	private LEA() {
		throw new AssertionError();
	}

	public static final BlockCipher getEngine() {
		return new LeaEngine();
	}

	public static final class ECB extends ECBMode {
		public ECB() {
			super(getEngine());
		}
	}

	public static final class CBC extends CBCMode {
		public CBC() {
			super(getEngine());
		}
	}

	public static final class CTR extends CTRMode {
		public CTR() {
			super(getEngine());
		}
	}

	public static final class CFB extends CFBMode {
		public CFB() {
			super(getEngine());
		}
	}

	public static final class OFB extends OFBMode {
		public OFB() {
			super(getEngine());
		}
	}

	public static final class CCM extends CCMMode {
		public CCM() {
			super(getEngine());
		}
	}

	public static final class GCM extends GCMMode {
		public GCM() {
			super(getEngine());
		}
	}

	public static final class CMAC extends CMac {
		public CMAC() {
			super(getEngine());
		}
	}

}
