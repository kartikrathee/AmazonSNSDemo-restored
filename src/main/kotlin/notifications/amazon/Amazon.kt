/*
MIT License

Copyright (c) 2017 Marcel Braghetto

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package notifications.amazon

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.sns.AmazonSNSClientBuilder
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest
import com.amazonaws.services.sns.model.PublishRequest
import notifications.framework.Jackson
import notifications.framework.Platform
import notifications.framework.toJson
import notifications.repository.Registration
import notifications.repository.RegistrationRepository

object Amazon {
    /*

    You will need to obtain platform ARNs for Android / iOS here by creating them in your
    Amazon SNS console with the 'Create platform application' flow. Your AWS account might not use
    'southeast-2' but the SNS platform applications console link will look something like this:

    https://ap-southeast-2.console.aws.amazon.com/sns/v2/home?region=ap-southeast-2#/applications

    Typically after creating a platform for both iOS and Android, their platform ARNs look similar to:

    arn:aws:sns:ap-southeast-2:000000000000:app/GCM/Push-Notifications-Android
    arn:aws:sns:ap-southeast-2:000000000000:app/APNS_SANDBOX/Push-Notifications-Apple

    Just copy them from the Amazon console after creating them.
    */

    private val androidPlatformArn = "- Enter your Android platform ARN from AWS console -"
    private val iosPlatformArn = "- Enter your iOS platform ARN from AWS console -"

    /*
    Accessing the Amazon APIs requires credentials aligned with your AWS account.

    One way to use credentials is through an AWS user account. To create an AWS user account go here:

    http://docs.aws.amazon.com/IAM/latest/UserGuide/id_users_create.html

    Create a new user, then assign them into a new group that has SNS access rights (you might have to
    create a new group with Amazon SNS rights). After creating that user, you will need to copy the
    'access key' and the 'secret key'. Likely you will have a secure strategy for storing these credentials
    in your server environment but for this simple demo they are just defined inline below:
     */
    private val accessKey = "- Enter your access key for your AWS SNS user -"
    private val secretKey = "- Enter your secret key for your AWS SNS user -"

    private val snsClient by lazy {
        // Requests to Amazon can be done through an Amazon SNS client. It is a heavy process to initialise
        // one so we can just use a single instance of it.
        AmazonSNSClientBuilder.standard()
                .withRegion(Regions.AP_SOUTHEAST_2)
                .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)))
                .build()
    }

    // Registration of a mobile device requires knowledge of which platform (eg iOS/Android) along with
    // the relevent platform specific push notification token supplied by the client operating system.
    fun registerEndPoint(platform: Platform, userId: String, token: String): Boolean {
        val request = CreatePlatformEndpointRequest().apply {
            this.token = token
            this.platformApplicationArn = when (platform) {
                Platform.ANDROID -> androidPlatformArn
                Platform.IOS -> iosPlatformArn
            }
        }

        try {
            println("About to register user '$userId' for endpoint: $token")
            // 'Registering' a new push token is done by creating a new 'endpoint' within Amazon SNS.
            // The creation of the endpoint will return an ARN which can then be stored in our own
            // server. That endpoint ARN can then be used to target push notifications.
