package com.edfapay.fplugin

import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import com.edfapay.paymentcard.bridge.EdfaPaySoftPosBridgeChannel
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


private typealias Promise = Result
private fun Promise.reject(exception:Throwable) = error("SDK Activity Failed", exception.message, exception.stackTraceToString())
private fun Promise.resolve(result: Any) = success(result)
private fun stackTrace() = Thread.currentThread().stackTrace.joinToString { "\n" }
/** FlutterEdfapayPlugin */
class FlutterEdfapayPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {

  private lateinit var methodChannel : MethodChannel
  private var activity: FragmentActivity? = null
  private val sdkBridge by lazy { EdfaPaySoftPosBridgeChannel(activity!!) }

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
      methodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, "com.edfapay.fplugin.method")
      methodChannel.setMethodCallHandler(this)
  }


  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
      if(activity !is FragmentActivity){
          result.error("Assertion Failed", "Implement the 'FlutterFragmentActivity' instead of 'FlutterActivity' with your MainActivity file for android module", stackTrace())
          return
      }

      if(call.arguments !is String){
          result.error("Invalid Argument", "Argument should be string", "Argument should be string");
          return
      }

      val arg = (call.arguments as String)

      if (call.method == "getPlatformVersion") {
          result.success("Android ${android.os.Build.VERSION.RELEASE}")

      }else{
          val methods = sdkBridge.javaClass.declaredMethods
          methods.firstOrNull { it.name == call.method }?.let {
              it.invoke(sdkBridge, arg, result::resolve, result::reject)
          } ?: result.notImplemented()
      }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    methodChannel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    if(binding.activity !is FragmentActivity){
      throw Exception("Implement the 'FlutterFragmentActivity' instead of 'FlutterActivity' with your MainActivity file for android module")
    }
    activity = binding.activity as FragmentActivity
  }

  override fun onDetachedFromActivityForConfigChanges() {
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
  }

  override fun onDetachedFromActivity() {
  }
}
