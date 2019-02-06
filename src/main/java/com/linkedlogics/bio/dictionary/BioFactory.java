package com.linkedlogics.bio.dictionary;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

import com.linkedlogics.bio.BioEnum;
import com.linkedlogics.bio.BioObject;

/**
 * Factory class for creating bio objects using dictionaru
 * @author rajab
 *
 */
public class BioFactory {
	private BioDictionary dictionary ;

	BioFactory(BioDictionary dictionary) {
		this.dictionary = dictionary ;
	}

	public BioObject newBioObject(int code) {
		BioObj obj = dictionary.getCodeMap().get(code);
		if (obj != null && obj.getBioClass() != null) {
			try {
				BioObject object = obj.getBioClass().getConstructor().newInstance();
				object.setBioCode(obj.getCode());
				object.setBioName(obj.getName());
				object.setBioDictionary(obj.getDictionary());
				return object ;
			} catch (Throwable e) {
				throw new RuntimeException(e) ;
			}
		}
		return null;
	}

	public BioObject[] newBioObjectArray(int code, int size) {
		BioObj obj = dictionary.getCodeMap().get(code);
		if (obj != null && obj.getBioClass() != null) {
			try {
				return (BioObject[]) Array.newInstance(obj.getBioClass(), size);
			} catch (Throwable e) {
				throw new RuntimeException(e) ;
			}
		}
		return null;
	}

	public BioObject newBioObject(int code, BioObject source) {
		BioObj obj = dictionary.getCodeMap().get(code);
		if (obj != null && obj.getBioClass() != null) {
			try {
				Constructor<BioObject> constructor;
				BioObject object = null ;
				try {
					constructor = obj.getBioClass().getConstructor(BioObject.class);
					object = constructor.newInstance(source) ;
				} catch (NoSuchMethodException e) {
					constructor = obj.getBioClass().getConstructor();
					object = constructor.newInstance() ;
					object.putAll(source) ;
				}
				object.setBioCode(obj.getCode());
				object.setBioName(obj.getName());
				object.setBioDictionary(obj.getDictionary());
				return object ;
			} catch (Throwable e) {
				throw new RuntimeException(e) ;
			}
		}
		return null;
	}

	public <T extends BioObject> T newBioObject(int code, Class<T> bioClass) {
		BioObj obj = dictionary.getCodeMap().get(code);
		if (obj != null && obj.getBioClass() != null) {
			try {
				T object = (T) obj.getBioClass().getConstructor().newInstance();
				object.setBioCode(obj.getCode());
				object.setBioName(obj.getName());
				object.setBioDictionary(obj.getDictionary());
				return object ;
			} catch (Throwable e) {
				throw new RuntimeException(e) ;
			}
		}
		return null;
	}

	public <T extends BioObject> T newBioObject(int code, BioObject source, Class<T> bioClass) {
		BioObj obj = dictionary.getCodeMap().get(code);
		if (obj != null && obj.getBioClass() != null) {
			try {
				Constructor<BioObject> constructor;
				T object = null ;
				try {
					constructor = obj.getBioClass().getConstructor(BioObject.class);
					object = (T) constructor.newInstance(source) ;
				} catch (NoSuchMethodException e) {
					constructor = obj.getBioClass().getConstructor();
					object = (T) constructor.newInstance() ;
					object.putAll(source) ;
				}
				object.setBioCode(obj.getCode());
				object.setBioName(obj.getName());
				object.setBioDictionary(obj.getDictionary());
				return object ;
			} catch (Throwable e) {
				throw new RuntimeException(e) ;
			}
		}
		return null;
	}

	public BioObject newBioObject(String type) {
		BioObj obj = dictionary.getTypeMap().get(type);
		if (obj != null && obj.getBioClass() != null) {
			try {
				BioObject object = obj.getBioClass().getConstructor().newInstance();
				object.setBioCode(obj.getCode());
				object.setBioName(obj.getName());
				object.setBioDictionary(obj.getDictionary());
				return object ;
			} catch (Throwable e) {
				throw new RuntimeException(e) ;
			}
		}
		return null;
	}

	public BioObject[] newBioObjectArray(String type, int size) {
		BioObj obj = dictionary.getTypeMap().get(type);
		if (obj != null && obj.getBioClass() != null) {
			try {
				return (BioObject[]) Array.newInstance(obj.getBioClass(), size);
			} catch (Throwable e) {
				throw new RuntimeException(e) ;
			}
		}
		return null;
	}

	public BioObject newBioObject(String type, BioObject source) {
		BioObj obj = dictionary.getTypeMap().get(type);
		if (obj != null && obj.getBioClass() != null) {
			try {
				Constructor<BioObject> constructor;
				BioObject object = null ;
				try {
					constructor = obj.getBioClass().getConstructor(BioObject.class);
					object = constructor.newInstance(source) ;
				} catch (NoSuchMethodException e) {
					constructor = obj.getBioClass().getConstructor();
					object = constructor.newInstance() ;
					object.putAll(source) ;
				}
				
				object.setBioCode(obj.getCode());
				object.setBioName(obj.getName());
				object.setBioDictionary(obj.getDictionary());
				return object ;
			} catch (Throwable e) {
				throw new RuntimeException(e) ;
			}
		}
		return null;
	}


	public <T extends BioObject> T newBioObject(String type, BioObject source, Class<T> bioClass) {
		BioObj obj = dictionary.getTypeMap().get(type);
		if (obj != null && obj.getBioClass() != null) {
			try {
				Constructor<BioObject> constructor;
				T object = null ;
				try {
					constructor = obj.getBioClass().getConstructor(BioObject.class);
					object = (T) constructor.newInstance(source) ;
				} catch (NoSuchMethodException e) {
					constructor = obj.getBioClass().getConstructor();
					object = (T) constructor.newInstance() ;
					object.putAll(source) ;
				}
				object.setBioCode(obj.getCode());
				object.setBioName(obj.getName());
				object.setBioDictionary(obj.getDictionary());
				return object ;
			} catch (Throwable e) {
				throw new RuntimeException(e) ;
			}
		}
		return null;
	}

	public BioEnum[] newBioEnumArray(int code, int size) {
		BioEnumObj enumObj = dictionary.getEnumCodeMap().get(code);
		if (enumObj != null && enumObj.getBioClass() != null) {
			try {
				return (BioEnum[]) Array.newInstance(enumObj.getBioClass(), size);
			} catch (Throwable e) {
				throw new RuntimeException(e) ;
			}
		}
		return null;
	}
}
