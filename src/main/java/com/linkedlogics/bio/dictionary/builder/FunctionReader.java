package com.linkedlogics.bio.dictionary.builder;

import com.linkedlogics.bio.BioDictionaryBuilder;
import com.linkedlogics.bio.BioFunction;
import com.linkedlogics.bio.annotation.BioFunc;
import com.linkedlogics.bio.exception.DictionaryException;
import com.linkedlogics.bio.expression.parser.BioFunctionManager;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

/**
 * Bio functions reader from annotations
 * @author rajab
 *
 */
public class FunctionReader implements DictionaryReader {
	private String packageName ;

	public FunctionReader(String packageName) {
		this.packageName = packageName ;
	}

	@Override
	public void read(BioDictionaryBuilder builder) {
		try (ScanResult scanResult =
				new ClassGraph()
				.enableAnnotationInfo()
				.enableClassInfo()
				.whitelistPackages(packageName)
				.scan()) {

			// Finding all bio objects
			for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(BioFunc.class.getName())) {
				if (!checkProfile(classInfo.getName(), builder.getProfiles(), builder.isOnlyProfiles())) {
					continue ;
				}
				
				createFunc(classInfo.getName());
			}
		} catch (Throwable e) {
			throw new DictionaryException(e) ;
		}
	}
	
	private void createFunc(String functionClassName) {
		try {
			Class<? extends BioFunction> functionClass = (Class<? extends BioFunction>) Class.forName(functionClassName);
			BioFunc annotation = (BioFunc) functionClass.getAnnotation(BioFunc.class);
			BioFunctionManager.setFunction(annotation.name(), functionClass, annotation.isCached());
		} catch (ClassNotFoundException e) {
			throw new DictionaryException(e) ;
		} catch (ClassCastException e) {
			throw new DictionaryException(functionClassName + " must implement " + BioFunction.class.getName(), e) ;
		}
	}
}
