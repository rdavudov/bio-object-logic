package com.linkedlogics.bio.dictionary.builder;

import java.lang.reflect.Field;
import java.util.List;

import com.linkedlogics.bio.BioDictionary;
import com.linkedlogics.bio.BioDictionaryBuilder;
import com.linkedlogics.bio.BioEnum;
import com.linkedlogics.bio.BioFunction;
import com.linkedlogics.bio.BioObject;
import com.linkedlogics.bio.annotation.BioRemoteObj;
import com.linkedlogics.bio.annotation.BioRemoteTag;
import com.linkedlogics.bio.annotation.BioSuperObj;
import com.linkedlogics.bio.dictionary.BioEnumObj;
import com.linkedlogics.bio.dictionary.BioFunc;
import com.linkedlogics.bio.dictionary.BioObj;
import com.linkedlogics.bio.dictionary.BioTag;
import com.linkedlogics.bio.dictionary.BioType;
import com.linkedlogics.bio.exception.DictionaryException;
import com.linkedlogics.bio.exception.ExpressionException;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

/**
 * BioDictionary reader from annotations
 * @author rajab
 *
 */
public class AnnotationReader implements DictionaryReader {
	private String packageName ;

	public AnnotationReader(String packageName) {
		this.packageName = packageName ;
	}

	@Override
	public void read(BioDictionaryBuilder builder) {
		try (ScanResult scanResult =
				new ClassGraph()
				.enableAnnotationInfo()
				.enableClassInfo()
				.enableFieldInfo()
				.whitelistPackages(packageName)
				.scan()) {

			// Finding all bio objects
			for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(com.linkedlogics.bio.annotation.BioObj.class.getName())) {
				if (!checkProfile(classInfo.getName(), builder.getProfiles(), builder.isOnlyProfiles())) {
					continue ;
				}
				BioObj obj = createObj(classInfo.getName());
				if (obj != null) {
					BioDictionary.getOrCreateDictionary(obj.getDictionary()).addObj(obj);
				}
			}

			// Finding all bio enums
			for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(com.linkedlogics.bio.annotation.BioEnumObj.class.getName())) {
				if (!checkProfile(classInfo.getName(), builder.getProfiles(), builder.isOnlyProfiles())) {
					continue ;
				}
				BioEnumObj enumObj = createEnum(classInfo.getName());
				if (enumObj != null) {
					BioDictionary.getOrCreateDictionary(enumObj.getDictionary()).addEnumObj(enumObj);
				}
			}

			// Finding all super tag contained objects
			for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(com.linkedlogics.bio.annotation.BioSuperObj.class.getName())) {
				if (!checkProfile(classInfo.getName(), builder.getProfiles(), builder.isOnlyProfiles())) {
					continue ;
				}
				Class bioSuperClass = classInfo.loadClass() ;
				BioSuperObj annotation = (BioSuperObj) bioSuperClass.getAnnotation(BioSuperObj.class) ;
				Field[] fields = bioSuperClass.getDeclaredFields();
				for (int j = 0; j < fields.length; j++) {
					if (fields[j].isAnnotationPresent(com.linkedlogics.bio.annotation.BioSuperTag.class)) {
						try {
							BioTag tag = createSuperTag(fields[j]);
							if (tag.getCode() > -1) {
								throw new DictionaryException("super tag code must be less than 0 " + tag.getName());
							}
							BioDictionary.getOrCreateDictionary(annotation.dictionary()).addSuperTag(tag);
						} catch (Throwable e) {
							throw new DictionaryException(e) ;
						}
					}
				}
			}

			// Finding all remote tag contained objects
			for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(com.linkedlogics.bio.annotation.BioRemoteObj.class.getName())) {
				if (!checkProfile(classInfo.getName(), builder.getProfiles(), builder.isOnlyProfiles())) {
					continue ;
				}

				Class bioRemoteClass = classInfo.loadClass();
				BioRemoteObj remoteAnnotation = (BioRemoteObj) bioRemoteClass.getAnnotation(BioRemoteObj.class) ;
				Field[] fields = bioRemoteClass.getDeclaredFields();
				for (int j = 0; j < fields.length; j++) {
					if (fields[j].isAnnotationPresent(BioRemoteTag.class)) {
						try {
							BioRemoteTag annotation = fields[j].getAnnotation(BioRemoteTag.class);
							BioTag tag = createRemoteTag(annotation, fields[j]);
							BioObj obj = BioDictionary.getOrCreateDictionary(remoteAnnotation.dictionary()).getObjByType(annotation.obj()) ;
							if (obj != null) {
								obj.addTag(tag);
							}
						} catch (Throwable e) {
							throw new DictionaryException(e) ;
						}
					} else if (fields[j].isAnnotationPresent(com.linkedlogics.bio.annotation.BioRemoteTags.class)) {
						BioRemoteTag[] array = (BioRemoteTag[]) fields[j].getAnnotationsByType(BioRemoteTag.class) ;
						for (int k = 0; k < array.length; k++) {
							try {
								BioRemoteTag annotation = array[k] ;
								BioTag tag = createRemoteTag(annotation, fields[j]);
								BioObj obj = BioDictionary.getOrCreateDictionary(remoteAnnotation.dictionary()).getObjByType(annotation.obj()) ;
								if (obj != null) {
									obj.addTag(tag);
								}
							} catch (Throwable e) {
								throw new DictionaryException(e) ;
							}
						}
					}
				}
			}
			
			for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(com.linkedlogics.bio.annotation.BioFunc.class.getName())) {
				if (!checkProfile(classInfo.getName(), builder.getProfiles(), builder.isOnlyProfiles())) {
					continue ;
				}
				BioFunc func = createFunc(classInfo.getName());
				if (func != null) {
					BioDictionary.getOrCreateDictionary(func.getDictionary()).addFunc(func);
				}
			}
		}
	}

	/**
	 * Creates bio obj definition from static fields
	 * @param objClassName
	 * @return
	 */
	private BioObj createObj(String objClassName) {
		try {
			Class bioClass = Class.forName(objClassName);
			com.linkedlogics.bio.annotation.BioObj annotation = (com.linkedlogics.bio.annotation.BioObj) bioClass.getAnnotation(com.linkedlogics.bio.annotation.BioObj.class);

			Class nameClass = bioClass ;
			String name = null ;
			while (nameClass != null) {
				try {
					name = (String) nameClass.getDeclaredField("BIO_NAME").get(null) ;
					break ;
				} catch (NoSuchFieldException e) {
					nameClass = nameClass.getSuperclass() ;
				}
			}

			BioObj obj = new BioObj(annotation.dictionary(), annotation.code(), bioClass.getSimpleName(), name, annotation.version());
			obj.setBioClass(bioClass);
			obj.setLarge(annotation.isLarge());
			while (bioClass != BioObject.class && bioClass != null) {
				Field[] fields = bioClass.getDeclaredFields();
				for (int j = 0; j < fields.length; j++) {
					if (fields[j].isAnnotationPresent(com.linkedlogics.bio.annotation.BioTag.class)) {
						try {
							if (!bioClass.getName().equals(objClassName)) {
								com.linkedlogics.bio.annotation.BioTag tagAnnotation = fields[j].getAnnotation(com.linkedlogics.bio.annotation.BioTag.class);
								if (!tagAnnotation.isInheritable()) {
									continue ;
								}
							}

							BioTag tag = createTag(fields[j]);
							obj.addTag(tag);

						} catch (Throwable e) {
							throw new DictionaryException("error in adding tag for " + bioClass.getName(), e) ;
						}
					}
				}

				bioClass = bioClass.getSuperclass();
			}

			if (bioClass == null) {
				throw new DictionaryException("invalid @BioObj usage, " + objClassName + " does not extend BioObject") ;
			}

			return obj;
		} catch (Throwable e) {
			throw new DictionaryException(e) ;
		}
	}

	/**
	 * Creates bio tag from annotation information such as code, type etc.
	 * @param field
	 * @return
	 * @throws Throwable
	 */
	private static BioTag createTag(Field field) throws Throwable {
		com.linkedlogics.bio.annotation.BioTag annotation = field.getAnnotation(com.linkedlogics.bio.annotation.BioTag.class);
		BioType type = BioType.BioObject;
		try {
			type = Enum.valueOf(BioType.class, annotation.type());
		} catch (Exception e) {

		}
		BioTag tag = new BioTag(annotation.code(), (String) field.get(null), type);
		if (tag.getType() == BioType.BioObject) {
			tag.setObjName(annotation.type());
		}
		tag.setMandatory(annotation.isMandatory());
		tag.setEncodable(annotation.isEncodable());
		tag.setExportable(annotation.isExportable());
		tag.setArray(annotation.isArray());
		tag.setList(annotation.isList());

		if (type == BioType.JavaEnum || type == BioType.JavaObject) {
			tag.setJavaClass(annotation.javaClass());
		}

		if (type == BioType.BioMap) {
			tag.setUseKey(annotation.useKey());
		}

		if (type == BioType.BioList || annotation.isList()) {
			String sortKey = annotation.sortKey();
			if (sortKey != null) {
				tag.setSortKey(sortKey);
			}
		}

		String[] trimKeys = annotation.trimKeys();
		if (trimKeys != null && trimKeys.length > 0 && trimKeys[0].length() > 0) {
			tag.setTrimKeys(trimKeys);
		}

		String[] inverseTrimKeys = annotation.inverseTrimKeys();
		if (inverseTrimKeys != null && inverseTrimKeys.length > 0 && inverseTrimKeys[0].length() > 0) {
			tag.setInverseTrimKeys(inverseTrimKeys);
		}

		if (annotation.initial().length() > 0) {
			tag.setInitial(annotation.initial());
		} 
		if (annotation.expression().length() > 0) {
			tag.setExpression(annotation.expression());
		}

		return tag;
	}

	/**
	 * Creates bio super tag from annotation information such as code, type etc.
	 * @param field
	 * @return
	 * @throws Throwable
	 */
	public static BioTag createSuperTag(Field field) throws Throwable {
		com.linkedlogics.bio.annotation.BioSuperTag annotation = field.getAnnotation(com.linkedlogics.bio.annotation.BioSuperTag.class);
		BioType type = BioType.BioObject;
		try {
			type = Enum.valueOf(BioType.class, annotation.type());
		} catch (Exception e) {

		}
		BioTag tag = new BioTag(annotation.code(), (String) field.get(null), type);
		if (tag.getType() == BioType.BioObject) {
			tag.setObjName(annotation.type());
		}
		tag.setMandatory(annotation.isMandatory());
		tag.setEncodable(annotation.isEncodable());
		tag.setExportable(annotation.isExportable());
		tag.setArray(annotation.isArray());
		tag.setList(annotation.isList());

		if (type == BioType.JavaEnum || type == BioType.JavaObject) {
			tag.setJavaClass(annotation.javaClass());
		}

		if (type == BioType.BioMap) {
			tag.setUseKey(annotation.useKey());
		}

		if (type == BioType.BioList || annotation.isList()) {
			String sortKey = annotation.sortKey();
			if (sortKey != null) {
				tag.setSortKey(sortKey);
			}
		}

		String[] trimKeys = annotation.trimKeys();
		if (trimKeys != null && trimKeys.length > 0 && trimKeys[0].length() > 0) {
			tag.setTrimKeys(trimKeys);
		}

		String[] inverseTrimKeys = annotation.inverseTrimKeys();
		if (inverseTrimKeys != null && inverseTrimKeys.length > 0 && inverseTrimKeys[0].length() > 0) {
			tag.setInverseTrimKeys(inverseTrimKeys);
		}

		if (annotation.initial().length() > 0) {
			tag.setInitial(annotation.initial());
		} 
		if (annotation.expression().length() > 0) {
			tag.setExpression(annotation.expression());
		}

		return tag;
	}

	/**
	 * Creates bio remote tag from annotation information such as code, type etc.
	 * @param field
	 * @return
	 * @throws Throwable
	 */
	public static BioTag createRemoteTag(BioRemoteTag annotation, Field field) throws Throwable {
		BioType type = BioType.BioObject;
		try {
			type = Enum.valueOf(BioType.class, annotation.type());
		} catch (Exception e) {

		}
		BioTag tag = new BioTag(annotation.code(), (String) field.get(null), type);
		if (tag.getType() == BioType.BioObject) {
			tag.setObjName(annotation.type());
		}
		tag.setMandatory(annotation.isMandatory());
		tag.setEncodable(annotation.isEncodable());
		tag.setExportable(annotation.isExportable());
		tag.setArray(annotation.isArray());
		tag.setList(annotation.isList());

		if (type == BioType.JavaEnum || type == BioType.JavaObject) {
			tag.setJavaClass(annotation.javaClass());
		}

		if (type == BioType.BioMap) {
			tag.setUseKey(annotation.useKey());
		}

		if (type == BioType.BioList || annotation.isList()) {
			String sortKey = annotation.sortKey();
			if (sortKey != null) {
				tag.setSortKey(sortKey);
			}
		}

		String[] trimKeys = annotation.trimKeys();
		if (trimKeys != null && trimKeys.length > 0 && trimKeys[0].length() > 0) {
			tag.setTrimKeys(trimKeys);
		}

		String[] inverseTrimKeys = annotation.inverseTrimKeys();
		if (inverseTrimKeys != null && inverseTrimKeys.length > 0 && inverseTrimKeys[0].length() > 0) {
			tag.setInverseTrimKeys(inverseTrimKeys);
		}

		if (annotation.initial().length() > 0) {
			tag.setInitial(annotation.initial());
		} 
		if (annotation.expression().length() > 0) {
			tag.setExpression(annotation.expression());
		}

		return tag;
	}

	/**
	 * Creates bio enum definition
	 * @param enumClassName
	 * @return
	 */
	public static com.linkedlogics.bio.dictionary.BioEnumObj createEnum(String enumClassName) {
		try {
			Class bioClass = Class.forName(enumClassName);
			com.linkedlogics.bio.annotation.BioEnumObj annotation = (com.linkedlogics.bio.annotation.BioEnumObj) bioClass.getAnnotation(com.linkedlogics.bio.annotation.BioEnumObj.class);
			com.linkedlogics.bio.dictionary.BioEnumObj bioEnum = new com.linkedlogics.bio.dictionary.BioEnumObj(annotation.code(), bioClass.getSimpleName());
			bioEnum.setClassName(bioClass.getName());
			bioEnum.setDictionary(annotation.dictionary());
			while (bioClass != BioEnum.class && bioClass != null) {
				Field[] fields = bioClass.getDeclaredFields();
				for (int j = 0; j < fields.length; j++) {
					try {
						if (fields[j].get(null).getClass().equals(bioClass)) {
							BioEnum value = (BioEnum) fields[j].get(null);
							bioEnum.addValue(value);
						}
					} catch (Throwable e) {
						throw new DictionaryException(e) ;
					}
				}

				bioClass = bioClass.getSuperclass();
			}

			if (bioClass == null) {
				throw new DictionaryException("invalid @BioEnumObj usage, " + enumClassName + " does not extend BioEnum") ;
			}

			return bioEnum;
		} catch (ClassNotFoundException e) {
			throw new DictionaryException(e) ;
		}
	}
	
	/**
	 * Creates bio function definition
	 * @param funcClassName
	 * @return
	 */
	private BioFunc createFunc(String funcClassName) {
		try {
			Class funcClass = Class.forName(funcClassName);
			com.linkedlogics.bio.annotation.BioFunc annotation = (com.linkedlogics.bio.annotation.BioFunc) funcClass.getAnnotation(com.linkedlogics.bio.annotation.BioFunc.class);

			if (!BioFunction.class.isAssignableFrom(funcClass)) {
				throw new DictionaryException("invalid @BioFunc usage, " + funcClassName + " does not implement BioFunction") ;
			}
			
			BioFunc func = new BioFunc(annotation.name(), funcClass, annotation.isCached(), annotation.dictionary(), annotation.version()) ;
			
			if (func.isCached()) {
				try {
					BioFunction cached = (BioFunction) funcClass.getConstructor().newInstance() ;
					func.setCached(cached);
				} catch (InstantiationException e) {
					throw new ExpressionException("instantiation exception for " + funcClass.getName(), e) ;
				} catch (NoSuchMethodException e) {
					throw new ExpressionException("missing default constructor in " + funcClass.getName(), e) ;
				} catch (Throwable e) {
					throw new ExpressionException(e) ;
				}
			}
			
			return func ;
		} catch (Throwable e) {
			throw new DictionaryException(e) ;
		}
	}
}
