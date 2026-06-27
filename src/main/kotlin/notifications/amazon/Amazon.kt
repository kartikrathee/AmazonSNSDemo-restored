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

