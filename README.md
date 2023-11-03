It's a google photos clone thing.





# Morari Gheorghe report for LAB at ASS

# 1. Existing problem / Actuality of the project
The problem is that there are not many self-hosted on-device alternatives to photo applications like google photos that offer machine learning features like semantic filtering.
The subject is actual because there are a lot of open source project appearing trying to solve this problem, but most of them are self-hosted on a separate device.
The problem is one of privacy and control. These "free" services are using the photos for unknown usages, most likely compromising the user's privacy in the process, and more than that, many attackers intentionally target these kind of services due to the sensitivity of the data hosted on the servers. As more and more attacks are done on more services, it is unknown when such an attack will occur and if your photos will get public.

# 2. Potential solutions comparison
1. One potential potential solution is self-hosting on a separate device. But very few people can and have the option to self host, given the technical challenges, costs, and complexity involved. This option may not be practical for the majority of users.
2. Another potential solution is to use a more privacy focused service instead, but there is no guarantee of the long term commitment on privacy and activity of such a solution. Users face uncertainty about the safety of their photos over time.
3. A third solution can be paying for hosting, but many people don't want to pay for things they are getting for free right now. This resistance to payment may limit the adoption of this option, even if it offers better accountability and data security.

# 3. Selected solution description
The selected solution is to have an on-device fully offline alternative to google photos style of gallery.
This solution is using open source capabilities for on-device inference using android os resources.

# 4. Usage
The usage of this app is very simple, it is designed to mimick the usage of a simple gallery with a search bar.

# 5. Structure
The structure of the app is based on a MVVM architecture.
![ass_lab drawio](https://github.com/GheorgheMorari/ASSLab/assets/53918731/9237baea-64f1-4461-abcf-fc2087e8a287)

# 6. Benefits
1. All user data and photos remain on the user's device, eliminating concerns about data breaches, unauthorized access, or misuse of personal photos by third-party services. Users have full control over their data, reducing privacy risks.
2. The solution eliminates the need for constant internet connectivity and cloud storage, making it ideal for users in areas with limited or unreliable internet access. It also reduces the environmental footprint associated with cloud data centers.
3. Users do not need to pay recurring subscription fees for cloud-based photo services. This model can save users money over time while providing a sustainable and cost-effective alternative.
4. Users do not need to worry about the longevity or stability of third-party service providers. With an open-source, on-device solution, they have more control over the sustainability and future development of their photo management system.

# 7. Challenges
1. Limited processing power and storage capacity. Implementing advanced machine learning features, like semantic filtering, on these devices can be challenging, and it might require optimization for various hardware configurations.
2. Ensuring that these algorithms run efficiently on user devices without significantly draining battery life or slowing down other tasks is a technical challenge.
3. Designing a user-friendly and intuitive interface for the photo management application is essential. Users need to feel comfortable and confident in their ability to manage their photos effectively, which requires thoughtful UI/UX design.
4. This solution may provide reduced utility compared to established cloud based solutions because of the mismatch in compute resources. 

# 8. Tools
This project is using the Android software development toolkit provided by google and it is targeting a new version of android.
The inference is done using the graphics processing unit delegate of the tensorflow-lite framework which is capable of running on virtually all recent devices and "over 80% of Android devices" according to a statistic in 2019.

# 9. Examples

https://github.com/GheorgheMorari/ASSLab/assets/53918731/3f02562b-47c5-49b4-9afb-63d4273d88b3

# 10. Conclusion
The project can be expanded to behave more like an android gallery and to have more features present in cloud gallery providers and to offer a decent customer experience.
Feature parity is most likely impossible using only on-device processing, and may drastically increase the development costs.

The current state of the implementation is a good proof of concept that shows how on-device inference may be a viable alternative to cloud processing when it comes to semantic filtering photos in a gallery.
