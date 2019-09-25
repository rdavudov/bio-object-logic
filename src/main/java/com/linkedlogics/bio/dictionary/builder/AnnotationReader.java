package com.linkedlogics.bio.dictionary.builder;

import java.lang.reflect.Field;

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
import com.linkedlogics.bio.dictionary.MergeType;
import com.linkedlogics.bio.exception.DictionaryException;
import com.linkedlogics.bio.exception.ExpressionException;
import com.linkedlogics.bio.utility.POJOUtility;

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
	private BioDictionaryBuilder builder ;

	public AnnotationReader() {

	}
	
	public AnnotationReader(String packageName) {
		this.packageName = packageName ;
	}

	/**
	 * Reads dictionary from class path
	 */
	@Override
	public void read(BioDictionaryBuilder builder) {
		this.builder = builder ;
		ClassGraph graph = new ClassGraph().enableAnnotationInfo()
				.enableClassInfo()
				.enableFieldInfo();
				
		if (packageName != null) {
			graph.whitelistPackages(packageName) ;
		}
		
		try (ScanResult scanResult = graph.scan()) {
			// Finding all bio objects
			for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(com.linkedlogics.bio.annotation.BioObj.class.getName())) {
				if (!checkProfile(classInfo.getName(), builder.getProfiles(), builder.isOnlyProfiles())) {
					continue ;
				}
				BioObj obj = createObj(classInfo.getName());
				if (obj != null) {
					if (obj.getCode() == 0) {
						obj.setCode(builder.getTagHasher().hash(obj.getBioClass().getName()));
						obj.setCodeGenerated(true);
					}
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
					if (enumObj.getCode() == 0) {
						enumObj.setCode(builder.getTagHasher().hash(enumObj.getBioClass().getName()));
						enumObj.setCodeGenerated(true);
					}
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
							if (tag.getCode() == 0) {
								tag.setCode(-builder.getTagHasher().hash(tag.getName()));
								tag.setCodeGenerated(true);
							}
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
								if (tag.getCode() == 0) {
									tag.setCode(builder.getTagHasher().hash(tag.getName()));
									tag.setCodeGenerated(true);
								}
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
									if (tag.getCode() == 0) {
										tag.setCode(builder.getTagHasher().hash(tag.getName()));
										tag.setCodeGenerated(true);
									}
									obj.addTag(tag);
								}
							} catch (Throwable e) {
								throw new DictionaryException(e) ;
							}
						}
					}
				}
			}
			
			// Finds all functions
			for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(com.linkedlogics.bio.annotation.BioFunc.class.getName())) {
				if (!checkProfile(classInfo.getName(), builder.getProfiles(), builder.isOnlyProfiles())) {
					continue ;
				}
				BioFunc func = createFunc(classInfo.getName());
				if (func != null) {
					BioDictionary.getOrCreateDictionary(func.getDictionary()).addFunc(func);
				}
			}
			
			// Finding all bio pojo objects
			for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(com.linkedlogics.bio.annotation.BioPojo.class.getName())) {
				if (!checkProfile(classInfo.getName(), builder.getProfiles(), builder.isOnlyProfiles())) {
					continue ;
				}
				try {
					BioObj obj = POJOUtility.createObj(Class.forName(classInfo.getName()));
					if (obj != null) {
						BioDictionary.getOrCreateDictionary(obj.getDictionary()).addObj(obj);
					}
				} catch (ClassNotFoundException e) {
					throw new DictionaryException(e) ;
				}
			}
			
			// Finding all java enums
			for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(com.linkedlogics.bio.annotation.BioJavaEnum.class.getName())) {
				if (!checkProfile(classInfo.getName(), builder.getProfiles(), builder.isOnlyProfiles())) {
					continue ;
				}
				BioEnumObj enumObj = createJavaEnum(classInfo.getName());
				if (enumObj != null) {
					BioDictionary.getOrCreateDictionary(enumObj.getDictionary()).addEnumObj(enumObj);
				}
			}
		}
	}
	
	/**
	 * Creates bio obj object
	 * @param objClassName
	 * @return
	 */
	private BioObj createObj(String objClassName) {
		try {
			Class bioClass = Class.forName(objClassName);
			
			BioObj obj = new BioObj();
			obj.setBioClass(bioClass);
			
			boolean isAnnotatedCode = false ;
			boolean isAnnotatedName = false ;
			boolean isAnnotatedType = false ;
			boolean isAnnotatedDict = false ;
			
			// annotation information can be given or default values can be used
			// for code default value is hash function result
			// for type default value is bio object class name
			// for name default value is bio object class snake case
			while (bioClass != BioObject.class && bioClass != null) {
				com.linkedlogics.bio.annotation.BioObj annotation = (com.linkedlogics.bio.annotation.BioObj) bioClass.getAnnotation(com.linkedlogics.bio.annotation.BioObj.class);
				
				// if annotation is present we check
				if (annotation != null) {
					// if no code given from annotation before and current one contains code we use it and stop going further
					if (!isAnnotatedCode) {
						if (annotation.code() > 0) {
							obj.setCode(annotation.code());
							obj.setCodeGenerated(false);
							// else if it is not given then we generate a code using hash function 
						} else {
							obj.setCode(builder.getTagHasher().hash(bioClass.getName()));
							obj.setCodeGenerated(true);
						}
						isAnnotatedCode = true ;
					}
					
					if (!isAnnotatedName) {
						// if no name given from annotation before and current one contains name we use it and stop going further
						if (annotation.name().length() > 0) {
							obj.setName(annotation.name());
							// else if it is not given then we generate a snake case of class
						} else {
							obj.setName(bioClass.getSimpleName().replaceAll("([^_A-Z])([A-Z])", "$1_$2").toLowerCase());
						}
						isAnnotatedName = true ;
					}
					
					if (!isAnnotatedType) {
						// if no type given from annotation before and current one contains name we use it and stop going further
						if (annotation.type().length() > 0) {
							obj.setType(annotation.type());
							// else if it is not given then we use class name as type
						} else {
							obj.setType(bioClass.getSimpleName());
						}
						isAnnotatedType = true ;
					} else {
						// here we check if types of subclass and superclass are same
						String type = null ;
						if (annotation.type().length() > 0) {
							type = annotation.type();
						} else {
							type = bioClass.getSimpleName();
						}
						// if they are same then we copy code and name from parent and replace bio obj with more concrete one
						if (type.equals(obj.getType())) {
							if (obj.isCodeGenerated()) {
								if (annotation.code() > 0) {
									obj.setCode(annotation.code());
									obj.setCodeGenerated(false);
									// else if it is not given then we generate a code using hash function 
								} else {
									obj.setCode(builder.getTagHasher().hash(bioClass.getName()));
									obj.setCodeGenerated(true);
								}
							}
						}
					}

					// if no type given from annotation before and current one contains dictinary we use it and stop going further
					if (!isAnnotatedDict && annotation.dictionary() > 0) {
						obj.setDictionary(annotation.dictionary());
					}
					isAnnotatedDict = true ;
				}
				
				// here we create tags using fields having @BioTag annotation
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
							if (tag.getCode() == 0) {
								tag.setCode(builder.getTagHasher().hash(tag.getName()));
								tag.setCodeGenerated(true);
							}
							obj.addTag(tag);
						} catch (NullPointerException e) {
							throw new DictionaryException("error in adding tag " + fields[j].getName() + " for " + bioClass.getName() + " it is not defined as static", e) ;
						} catch (Throwable e) {
							throw new DictionaryException("error in adding tag " + fields[j].getName() + " for " + bioClass.getName(), e) ;
						}
					}
				}
				
				// go up in the hierarchy 
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
		
		return createTag((String) field.get(null), annotation.type(), annotation.code(), 
				annotation.isMandatory(), annotation.isEncodable(), annotation.isExportable(), 
				annotation.isArray(), annotation.isList(), annotation.isInheritable(), annotation.javaClass(), annotation.useKey(), 
				annotation.sortKey(), annotation.trimKeys(), annotation.inverseTrimKeys(), 
				annotation.initial(), annotation.expression(), annotation.mergeBy()) ;
	}

	/**
	 * Creates bio super tag from annotation information such as code, type etc.
	 * @param field
	 * @return
	 * @throws Throwable
	 */
	public static BioTag createSuperTag(Field field) throws Throwable {
		com.linkedlogics.bio.annotation.BioSuperTag annotation = field.getAnnotation(com.linkedlogics.bio.annotation.BioSuperTag.class);
		
		return createTag((String) field.get(null), annotation.type(), annotation.code(), 
				annotation.isMandatory(), annotation.isEncodable(), annotation.isExportable(), 
				annotation.isArray(), annotation.isList(), false, annotation.javaClass(), annotation.useKey(), 
				annotation.sortKey(), annotation.trimKeys(), annotation.inverseTrimKeys(), 
				annotation.initial(), annotation.expression(), annotation.mergeBy()) ;
	}

	/**
	 * Creates bio remote tag from annotation information such as code, type etc.
	 * @param field
	 * @return
	 * @throws Throwable
	 */
	public static BioTag createRemoteTag(BioRemoteTag annotation, Field field) throws Throwable {
		return createTag((String) field.get(null), annotation.type(), annotation.code(), 
				annotation.isMandatory(), annotation.isEncodable(), annotation.isExportable(), 
				annotation.isArray(), annotation.isList(), annotation.isInheritable(), annotation.javaClass(), annotation.useKey(), 
				annotation.sortKey(), annotation.trimKeys(), annotation.inverseTrimKeys(), 
				annotation.initial(), annotation.expression(), annotation.mergeBy()) ;
	}
	
	/**
	 * Creates bio tag
	 * @param name
	 * @param type
	 * @param code
	 * @param isMandatory
	 * @param isEncodable
	 * @param isExportable
	 * @param isArray
	 * @param isList
	 * @param javaClass
	 * @param useKey
	 * @param sortKey
	 * @param trimKeys
	 * @param inverseTrimKeys
	 * @param initial
	 * @param expression
	 * @return
	 */
	private static BioTag createTag(String name, String type, int code, boolean isMandatory, boolean isEncodable, boolean isExportable, boolean isArray, boolean isList, boolean isInheritable, 
			Class javaClass, String useKey, String sortKey, String[] trimKeys, String[] inverseTrimKeys, String initial, String expression, MergeType mergeType) {
		BioType bioType = BioType.BioObject;
		try {
			bioType = Enum.valueOf(BioType.class, type);
		} catch (Exception e) {

		}
		BioTag tag = new BioTag(code, name, bioType);
		if (tag.getType() == BioType.BioObject) {
			tag.setObjName(type);
		}
		tag.setMandatory(isMandatory);
		tag.setEncodable(isEncodable);
		tag.setExportable(isExportable);
		tag.setInheritable(isInheritable);
		tag.setArray(isArray);
		tag.setList(isList);

		if (bioType == BioType.JavaEnum || bioType == BioType.JavaObject) {
			tag.setJavaClass(javaClass);
		}

		if (trimKeys != null && trimKeys.length > 0 && trimKeys[0].length() > 0) {
			tag.setTrimKeys(trimKeys);
		}

		if (inverseTrimKeys != null && inverseTrimKeys.length > 0 && inverseTrimKeys[0].length() > 0) {
			tag.setInverseTrimKeys(inverseTrimKeys);
		}

		if (initial != null && initial.length() > 0) {
			tag.setInitial(initial);
		}
		
		if (expression != null && expression.length() > 0) {
			tag.setExpression(expression);
		}
		
		tag.setMergeType(mergeType);

		return tag;
	}

	/**
	 * Creates bio enum definition
	 * @param enumClassName
	 * @return
	 */
	public com.linkedlogics.bio.dictionary.BioEnumObj createEnum(String enumClassName) {
		try {
			Class bioClass = Class.forName(enumClassName);
			
			BioEnumObj enumObj = new BioEnumObj() ;
			enumObj.setBioClass(bioClass);
			enumObj.setClassName(bioClass.getName());
			
			boolean isAnnotatedCode = false ;
			boolean isAnnotatedName = false ;
			boolean isAnnotatedDict = false ;
			
			while (bioClass != BioEnum.class && bioClass != null) {
				com.linkedlogics.bio.annotation.BioEnumObj annotation = (com.linkedlogics.bio.annotation.BioEnumObj) bioClass.getAnnotation(com.linkedlogics.bio.annotation.BioEnumObj.class);
				// there was a case when enum is extended and added new enum values if don't below line
				// in dictionary enum bio class will most recent one and when try to create an enum array
				// enums create in super classes will not fit in this array because they will not be instance of 
				// this recent class. Therefore, each time we update bio class as parent but not BioEnum.class itself
				enumObj.setBioClass(bioClass);
				
				if (annotation != null) {
					if (!isAnnotatedCode && annotation.code() > 0) {
						enumObj.setCode(annotation.code());
						isAnnotatedCode = true ;
						enumObj.setCodeGenerated(false);
					} else {
						enumObj.setCode(builder.getTagHasher().hash(bioClass.getName()));
						enumObj.setCodeGenerated(true);
					}
					
					if (!isAnnotatedName && annotation.type().length() > 0) {
						enumObj.setType(annotation.type());
						isAnnotatedName = true ;
					} else {
						enumObj.setType(bioClass.getSimpleName());
					}
					
					if (!isAnnotatedDict && annotation.dictionary() > 0) {
						enumObj.setDictionary(annotation.dictionary());
						isAnnotatedDict = true ;
					}
				}
				
				Field[] fields = bioClass.getDeclaredFields();
				for (int j = 0; j < fields.length; j++) {
					try {
						if (fields[j].get(null).getClass().equals(bioClass)) {
							BioEnum value = (BioEnum) fields[j].get(null);
							enumObj.addValue(value);
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

			return enumObj;
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
	
	/**
	 * Creates bio obj definition from pojo declared fields
	 * @param objClassName
	 * @return
	 */
	private BioObj createPojoObj3(String objClassName) {
		try {
			Class bioClass = Class.forName(objClassName);
			com.linkedlogics.bio.annotation.BioPojo annotation = (com.linkedlogics.bio.annotation.BioPojo) bioClass.getAnnotation(com.linkedlogics.bio.annotation.BioPojo.class);

			int code = annotation.code() ;
			if (code == 0) {
				code = POJOUtility.getCode(objClassName) ;
			}
			String name = annotation.name() ;
			if (name.length() == 0) {
				name = bioClass.getSimpleName().replaceAll("([^_A-Z])([A-Z])", "$1_$2").toLowerCase() ;
			}

			BioObj obj = new BioObj(annotation.dictionary(), code, bioClass.getSimpleName(), name, annotation.version());
			obj.setBioClass(bioClass);
		
			while (bioClass != null) {
				if (bioClass.isAnnotationPresent(com.linkedlogics.bio.annotation.BioPojo.class)) {
					Field[] fields = bioClass.getDeclaredFields();
					for (int j = 0; j < fields.length; j++) {
						try {
							BioTag tag = POJOUtility.createPojoTag(fields[j]);
							obj.addTag(tag);
						} catch (Throwable e) {
							throw new DictionaryException("error in adding tag for " + bioClass.getName(), e) ;
						}
					}
				}

				bioClass = bioClass.getSuperclass();
			}

			return obj;
		} catch (Throwable e) {
			throw new DictionaryException(e) ;
		}
	}
	
	/**
	 * Converst java enum to bio enum
	 * @param enumClassName
	 * @return
	 */
	private static com.linkedlogics.bio.dictionary.BioEnumObj createJavaEnum(String enumClassName) {
		try {
			Class bioClass = Class.forName(enumClassName);
			com.linkedlogics.bio.annotation.BioJavaEnum annotation = (com.linkedlogics.bio.annotation.BioJavaEnum) bioClass.getAnnotation(com.linkedlogics.bio.annotation.BioJavaEnum.class);

			int code = annotation.code() ;
			if (code == 0) {
				code = POJOUtility.getCode(enumClassName) ;
			}
			String name = annotation.name() ;
			if (name.length() == 0) {
				name = bioClass.getSimpleName().replaceAll("([^_A-Z])([A-Z])", "$1_$2").toLowerCase() ;
			}
			
			com.linkedlogics.bio.dictionary.BioEnumObj bioEnum = new com.linkedlogics.bio.dictionary.BioEnumObj(code, bioClass.getSimpleName());
			bioEnum.setClassName(bioClass.getName());
			bioEnum.setDictionary(annotation.dictionary());
			
			Field[] fields = bioClass.getDeclaredFields() ;
			try {
				for (int i = 0; i < fields.length; i++) {
					if (fields[i].isEnumConstant()) {
						BioEnum e = new BioEnum(((Enum) fields[i].get(null)).ordinal(), ((Enum) fields[i].get(null)).name(), code) ;
						bioEnum.addValue(e) ;
					}
				}
			} catch (IllegalArgumentException e) {
				
			} catch (IllegalAccessException e) {
				
			}
			return bioEnum;
		} catch (ClassNotFoundException e) {
			throw new DictionaryException(e) ;
		}
	}
}
